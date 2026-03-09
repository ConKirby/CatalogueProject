import pandas as pd                                                               
import joblib    

def use_rfr(GDPPerCapita,Population,MalesPer100Females,perc18AndOver,IncarceratedPer100k,CorporationTax):                                                   
    X1 = [GDPPerCapita,Population,MalesPer100Females,perc18AndOver,IncarceratedPer100k,CorporationTax]
    rfr = joblib.load(r'models\rfr.joblib')

    #rfr_predictions1 = rfr.predict(X1)
    rfr_prediction = rfr.predict([X1])

    return rfr_prediction

import sys

if __name__ == "__main__":
    args = [float(a) for a in sys.argv[1:]]
    result = use_rfr(*args)
    print(result[0])