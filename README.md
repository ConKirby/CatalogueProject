# Country Catalogue

A country data catalogue that collects socioeconomic and military data from public sources and uses machine learning to predict missing firepower index values.

## What It Does

- **Scrapes data** from the World Bank API, Wikipedia, and Global Firepower for 211 countries
- **Trains a Random Forest model** on countries with known firepower indices
- **Predicts missing firepower values** for countries where data isn't available
- **Interactive Java app** lets you view, add, remove countries, and run predictions

## Data Collected

| Feature | Source |
|---|---|
| GDP Per Capita | World Bank API |
| Population | World Bank API |
| Males Per 100 Females | World Bank API |
| Age Demographics (Under 18 / 18+) | World Bank API |
| Incarceration Rate | Wikipedia |
| Corporation Tax | Wikipedia |
| Firepower Index | Global Firepower |

## The Prediction Model

We used a Random Forest Regressor to predict firepower index values. The model is trained on countries that already have a known firepower index, using six features as input: GDP per capita, population, males per 100 females, percentage aged 18 and over, incarceration rate per 100k, and corporation tax rate. Countries with missing values in any of these fields are excluded from training. The data is split 80/20 for training and testing, and the trained model is saved using joblib so it can be loaded later for predictions without retraining. When a user selects a country with a missing firepower index in the Java app, the app passes that country's features to the Python model, which returns a predicted value.

## How It Works

1. `scrape_countries.py` pulls data from multiple sources and outputs `countries.csv`
2. `TrainFirepower.py` trains a Random Forest Regressor on countries with complete data and saves the model to `models/rfr.joblib`
3. The Java app (`Main.java` + `UseRFR.java`) provides a menu interface and calls the Python model to predict firepower for countries with missing values

## Tech Stack

- **Python** — pandas, scikit-learn, BeautifulSoup, requests
- **Java** — console app with subprocess calls to Python for predictions

## What We Achieved

- Built a full data pipeline from scraping to prediction
- Collected data for 211 countries across 9 features from 3 different sources
- Trained a working ML model that predicts firepower index from socioeconomic indicators
- Integrated Python ML into a Java application using subprocess communication
- Handled real-world data issues like missing values, inconsistent country names, and multi-year data fallbacks
