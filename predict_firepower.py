import pandas as pd
import joblib

rfr = joblib.load(r'models\rfr.joblib')

'''Predictions'''
#NOTE No need to train the models or split the data into train and test

#rfr_predictions1 = rfr.predict(X1)
rfr_predictions2 = rfr.predict(X2)

from sklearn.metrics import mean_absolute_percentage_error, r2_score

model_mape = mean_absolute_percentage_error(y2, rfr_predictions2)
print(f'mean_absolute_percentage_error: {model_mape}')

model_r2 = r2_score(y2, rfr_predictions2)
print(f'R squared: {model_r2}')