VENV=.venv
PY=python

install:
	$(PY) -m venv $(VENV)
	$(VENV)/Scripts/pip install --upgrade pip
	$(VENV)/Scripts/pip install -r requirements.txt

run:
	uvicorn app.main:app --reload --port 8000

fmt:
	black app tests
	isort app tests

lint:
	flake8 app
	mypy app

test:
	pytest -q