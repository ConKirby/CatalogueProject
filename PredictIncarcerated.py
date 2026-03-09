# We currently have a CSV file, but there are NaN values
# I want to train an ML model to be able to fill in these blanks
# We will save these predictions in a different csv

from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestRegressor
from sklearn.metrics import mean_absolute_percentage_error, r2_score
import pandas as pd


df1 = pd.read_csv(r"countries.csv")

# Not needed for the model + and what we want to predict
X = df1.drop(columns=['Country'])
# Target on y axis
y = df1['2022']

# split the data into train and test
from sklearn.model_selection import train_test_split
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

from sklearn.ensemble import RandomForestRegressor
rfr = RandomForestRegressor()
rfr.fit(X_train, y_train)
rfr_prediction = rfr.predict(X_test)

rfr_mape = mean_absolute_percentage_error(y_test, rfr_prediction)
print(f'mean_absolute_percentage_error: {rfr_mape}')

rfr_r2 = r2_score(y_test, rfr_prediction)
print(f'R squared: {rfr_r2}')

'''import plotly.express as px
fig = px.scatter(x=y_test, y=rfr_prediction, labels={'x': 'Actual Price', 'y': 'Predicted Price'},  title='Random Forest Regressor Model Prediction')
fig.show()'''

import joblib
#saving model
joblib.dump(rfr, r'path\to\trained_models\rfr_model.joblib')