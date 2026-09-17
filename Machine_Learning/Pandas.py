from __future__ import annotations

import argparse
from pathlib import Path

import pandas as pd
from pandas.api.types import is_numeric_dtype


def has_outliers(series: pd.Series) -> bool:
	clean_series = series.dropna()
	if clean_series.empty or clean_series.nunique() < 2:
		return False

	q1 = clean_series.quantile(0.25)
	q3 = clean_series.quantile(0.75)
	iqr = q3 - q1

	if pd.isna(iqr) or iqr == 0:
		return False

	lower_bound = q1 - 1.5 * iqr
	upper_bound = q3 + 1.5 * iqr
	return bool(((clean_series < lower_bound) | (clean_series > upper_bound)).any())


def numeric_fill_value(series: pd.Series) -> tuple[float | int, str]:
	if has_outliers(series):
		return series.median(), "mediana"
	return series.mean(), "media"


def detect_datetime_series(series: pd.Series) -> pd.Series | None:
	non_null = series.dropna()
	if non_null.empty:
		return None

	text_series = non_null.astype(str).str.strip()
	date_mask = text_series.str.fullmatch(r"\d{4}-\d{2}-\d{2}")
	datetime_mask = text_series.str.fullmatch(r"\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}")

	if date_mask.all():
		parsed = pd.to_datetime(series, format="%Y-%m-%d", errors="coerce")
	elif datetime_mask.all():
		parsed = pd.to_datetime(series, format="%Y-%m-%d %H:%M:%S", errors="coerce")
	else:
		return None

	return parsed.astype("datetime64[ns]")


def datetime_fill_value(series: pd.Series) -> tuple[pd.Timestamp, str]:
	numeric_series = series.dropna().astype("int64")
	if has_outliers(numeric_series):
		fill_raw = numeric_series.median()
		method = "mediana"
	else:
		fill_raw = numeric_series.mean()
		method = "media"

	return pd.Timestamp(int(fill_raw), unit="ns"), method


def infer_datetime_format(series: pd.Series) -> str:
	sample = series.dropna().astype(str)
	if sample.str.contains(":", regex=False).any():
		return "%Y-%m-%d %H:%M:%S"
	return "%Y-%m-%d"


def process_file(csv_path: Path, output_dir: Path) -> dict[str, object]:
	dataframe = pd.read_csv(csv_path, low_memory=False)
	report: list[dict[str, object]] = []

	for column in dataframe.columns:
		null_count = int(dataframe[column].isna().sum())
		if null_count == 0:
			continue

		series = dataframe[column]

		if is_numeric_dtype(series):
			fill_value, method = numeric_fill_value(series)
			dataframe[column] = series.fillna(fill_value)
			report.append(
				{
					"coluna": column,
					"nulos_preenchidos": null_count,
					"tipo": "numerico",
					"metodo": method,
					"valor_utilizado": fill_value,
				}
			)
			continue

		datetime_series = detect_datetime_series(series)
		if datetime_series is not None:
			fill_value, method = datetime_fill_value(datetime_series)
			filled_series = datetime_series.fillna(fill_value)
			dataframe[column] = filled_series.dt.strftime(infer_datetime_format(series))
			report.append(
				{
					"coluna": column,
					"nulos_preenchidos": null_count,
					"tipo": "datetime",
					"metodo": method,
					"valor_utilizado": fill_value,
				}
			)
			continue

		report.append(
			{
				"coluna": column,
				"nulos_preenchidos": 0,
				"tipo": str(series.dtype),
				"metodo": "nao_aplicavel",
				"valor_utilizado": None,
			}
		)

	output_dir.mkdir(parents=True, exist_ok=True)
	output_path = output_dir / csv_path.name
	dataframe.to_csv(output_path, index=False)

	return {
		"arquivo": csv_path.name,
		"saida": str(output_path),
		"colunas": report,
	}


def process_directory(input_dir: Path, output_dir: Path) -> list[dict[str, object]]:
	results = []
	for csv_path in sorted(input_dir.glob("*.csv")):
		results.append(process_file(csv_path, output_dir))
	return results


def print_report(results: list[dict[str, object]]) -> None:
	for result in results:
		print(f"\nArquivo: {result['arquivo']}")
		print(f"Saida: {result['saida']}")

		columns = result["colunas"]
		if not columns:
			print("  Nenhuma coluna com nulos encontrada.")
			continue

		for column in columns:
			print(
				"  - Coluna: {coluna} | tipo: {tipo} | metodo: {metodo} | "
				"nulos preenchidos: {nulos_preenchidos} | valor: {valor_utilizado}".format(
					**column
				)
			)


def build_parser() -> argparse.ArgumentParser:
	parser = argparse.ArgumentParser(
		description=(
			"Preenche valores nulos em arquivos CSV usando mediana quando ha outliers "
			"e media quando nao ha outliers."
		)
	)
	parser.add_argument(
		"--input-dir",
		type=Path,
		default=Path(__file__).resolve().parent / "csv",
		help="Diretorio com os arquivos CSV de entrada.",
	)
	parser.add_argument(
		"--output-dir",
		type=Path,
		default=Path(__file__).resolve().parent / "csv_preenchidos",
		help="Diretorio onde os arquivos tratados serao salvos.",
	)
	return parser


def main() -> None:
	parser = build_parser()
	args = parser.parse_args()

	if not args.input_dir.exists():
		raise FileNotFoundError(f"Diretorio nao encontrado: {args.input_dir}")

	results = process_directory(args.input_dir, args.output_dir)
	print_report(results)


if __name__ == "__main__":
	main()
