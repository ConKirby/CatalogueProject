# We currently have a CSV file, but there are NaN values
# I want to train an ML model to be able to fill in these blanks
# We will save these predictions in a different csv

from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestRegressor
from sklearn.metrics import mean_absolute_percentage_error, r2_score
import pandas as pd

df = pd.read_csv(r"countries.csv")

# Preprocessing to remove the N/A lines from Firepower
df = df.replace("N/A", pd.NA)
df = df.dropna()

print(df.head)


# Not needed for the model + and what we want to predict
X = df.drop(columns=['Country', '% Under 18', 'Firepower Index'])
# Target on y axis
y = df['Firepower Index']

# split the data into train and test
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

rfr = RandomForestRegressor()
rfr.fit(X_train, y_train)
rfr_prediction = rfr.predict(X_test)

rfr_mape = mean_absolute_percentage_error(y_test, rfr_prediction)
print(f'mean_absolute_percentage_error: {rfr_mape}')

rfr_r2 = r2_score(y_test, rfr_prediction)
print(f'R squared: {rfr_r2}')

import matplotlib.pyplot as plt                                                   
                                                                                
feature_importance = pd.Series(rfr.feature_importances_, index=X.columns)
feature_importance = feature_importance.sort_values(ascending=True)

feature_importance.plot(kind='barh')
plt.title('Feature Importance for Firepower Index')
plt.tight_layout()
plt.show()


import joblib
#saving model
joblib.dump(rfr, r'models//rfr.joblib')