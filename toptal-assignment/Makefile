requirements.txt:
	pipenv requirements > requirements.txt

requirements-dev.txt:
	pipenv requirements --dev > requirements-dev.txt

.PHONY:
clean:
	rm requirements.txt
	rm requirements-dev.txt

.PHONY:
test:
	pytest

.PHONY:
coverage-report: coverage
	coverage report -m

.PHONY:
coverage:
	coverage run -m pytest