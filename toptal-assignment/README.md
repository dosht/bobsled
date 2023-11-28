# Data engineer. Take home project

1. [Problem description](#problem-description)
2. [Execution instructions and notes](#execution-instructions-and-notes)
3. [Basic Solution](#basic-solution)
4. [Parallel Solution](#parallel-solution)

## Problem description
**Applepen** is a large network of retail stores that sells just 2 kinds of goods, namely **apple**s and **pen**s.
It has a number of retail stores in different parts of United States, and it operates for more than 10 years with great success.

Recently upper management decided to enhance their decision making with data.
Each store collects information on:

1. procurement (bi-monthly supply of apples and pens),
2. sales (transaction log of sold items), and
3. inventory (monthly snapshot with a number of apple and pen).

Data is available in the form of CSV files (a link for download will be given later). File data is sorted by date.

Unfortunately this information was never consolidated or reconciled.
For a starter, we would like to get the following data in csv file format:

### 1. Daily inventory for each store
Get data on inventory levels by the end of the day after all the procurement and sales has happened. This should be of great value to retail store managers. This data should take into account the monthly inventory snapshot. We know that people steal from retail store, but currently there is no visibility on how much is actually stolen.

Data should have the following shape:

|date|apple|pen|
|---|---|---|
|2006-01-01|14105|770|
|...|||

### 2. Monthly amount of stolen goods for each store.

Data should have the following shape:

|date|apple|pen|
|---|---|---|
|2006-01-31|4|4|
|2006-02-28|5|0|
|...|||


### 3. High level overview with sales volume and amount of stolen goods by state and year.

Data should have the following shape:

|year|state|apple_sold|apple_stolen|pen_sold|pen_stolen|
|---|---|---|---|---|---|
|2006|AK|4|2|2|1
|2007|AL|5|0|6|2|
|...||||||

## Solution considerations

1. We don't restrict you in regard to technologies, programming languages or libraries -- use whatever tool you need.
2. Team should be able to re-run your solution and get same results.
Solution may involve some amount of manual work, but all the steps should be clearly stated at the end of this README file.
3. Write and share output csv files in a directory of this github repository. Explicitly mention the directory path in next session (Execution instructions and notes).

## Execution instructions and notes

1. Configure gcloud with the appropriate user by running the following command and selecting the desired user account:

```bash
gcloud auth login
```
This step is required to ensure that you have the necessary permissions to access the GCS buckets.

2. Authenticate gcloud by running the following command and following the authentication process:

```bash
gcloud auth application-default login
```
This step is necessary to establish credentials for application-default authentication.

3. Run the [`download_data.sh`](/scripts/download_data.sh) script to download the CSV files from GCS. This script will download the files to the local machine. Execute the following command:

```bash
scripts/download_data.sh data/
```
The downloaded files will be stored in the specified directory.

4. Run the [`validate_downloaded_files.sh`](/scripts/validate_downloaded_files.sh) script to validate the downloaded files' checksums. This script will compare the MD5 checksums of the downloaded files with the expected checksums. Execute the following command:

```bash
scripts/validate_downloaded_files.sh data/
```
The script will display whether the checksums of the downloaded files match the expected checksums.

## Basic Solution

The basic solution is based on Pandas and runs sequentially by iterating over the downloaded CSV files. It can be installed as a package using `python setup.py install`. The main logic can be found in [`processor.py`](/applepen/processor.py) and [`validator.py`](/applepen/validator.py)

To run this solution, you should follow one of the following options:

### (Option 1) Install dependencies using Pipenv:
 - Ensure you have Pipenv installed. If not, install Pipenv by running:

```bash
pip install pipenv
```
 - Navigate to the project directory and run the following command to create a virtual environment and install the dependencies:

```bash
pipenv install --dev
```

 - Activate the virtual environment:

```bash
pipenv shell
```

### (Option 2) Install dependencies using Pip:

 - Create a virtual environment using your preferred method (e.g., venv, conda).
 - Activate the virtual environment.
 - Navigate to the project directory.
 - Install the dependencies from the requirements.txt file:

```bash
pip install -r requirements.txt
```

### Now, you can run the following command to execute the main script:

```bash
python -m applepen -i data -o output
```

Additionally, you can run it either for processing only or validation only by passing `--process-only` or `--validate-only` respectively.

This command will run the main script of the project, which will perform the data processing, and save the results under `output` directory.

### For running tests

for running tests make sure you install the dev dependencies using `pipenv install --dev` or `pip install -r requirements-dev.txt`

```bash
make test  # runs all test
make coverage-report # runs tests with coverage report
```

The tests use pytest and include only one example to demonstrate the way of creating fixtures and unit tests.

## Parallel Solution

This is a preliminary solution implemented using Pyspark. It demonstrates the approach I adopted to execute the processing of each store in parallel. The solution is designed to run on a cloud platform, utilizing a service account that possesses the necessary permissions to access the GCS bucket.

The code for the solution can be found in two files: [`parallel_processor.py`](/applepen/parallel_processor.py) and [`udf.py`](/applepen/udf.py). Due to the time constraints of the test, I have included placeholders (TODOs) for the remaining tasks, which will be similar to the Pandas-based solution.
