import requests
import csv
import re
import time
from bs4 import BeautifulSoup

NA = "N/A"
WIKI_HEADERS = {
    "User-Agent": "CountryCatalogueBot/1.0 (educational project)",
    "Accept": "text/html",
}
OUTPUT_FILE = "countries.csv"
HEADERS = [
    "Country",
    "GDP Per Capita (USD)",
    "Population",
    "Males Per 100 Females",
    "% Under 18",
    "% 18 And Over",
    "Incarcerated Per 100k",
    "Firepower Index",
    "Corporation Tax (%)",
]


def fetch_world_bank(indicator, date="2022"):
    """Fetch data from World Bank API for a given indicator."""
    url = f"https://api.worldbank.org/v2/country/all/indicator/{indicator}"
    params = {"format": "json", "per_page": 300, "date": date}
    data = {}
    try:
        resp = requests.get(url, params=params, timeout=30)
        resp.raise_for_status()
        result = resp.json()
        if len(result) > 1:
            for entry in result[1]:
                if entry["value"] is not None:
                    data[entry["country"]["value"]] = entry["value"]
    except Exception as e:
        print(f"  Warning: World Bank fetch failed for {indicator}: {e}")
    return data


def fetch_world_bank_multi_year(indicator, years=("2022", "2021", "2020", "2019")):
    """Try multiple years, falling back to older data if recent is sparse."""
    combined = {}
    for year in years:
        yearly = fetch_world_bank(indicator, date=year)
        for country, value in yearly.items():
            if country not in combined:
                combined[country] = value
    return combined


def fetch_firepower_index():
    """Scrape GlobalFirepower index scores."""
    data = {}
    url = "https://www.globalfirepower.com/countries-listing.php"
    try:
        resp = requests.get(url, timeout=30, headers={
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
        })
        resp.raise_for_status()
        soup = BeautifulSoup(resp.text, "html.parser")
        # Links contain country name + PwrIndx score in their text
        for link in soup.select('a[href*="-military-strength"]'):
            text = link.get_text(strip=True)
            # Text looks like: "1United StatesUSAPwrIndx: 0.0741"
            match = re.search(r'PwrIndx:\s*([0-9.]+)', text)
            if match:
                score = match.group(1)
                # Extract country name: strip leading rank number and trailing code+PwrIndx
                name_part = re.sub(r'^\d+', '', text)
                name_part = re.sub(r'[A-Z]{3}PwrIndx.*', '', name_part).strip()
                if name_part:
                    data[name_part] = score
    except Exception as e:
        print(f"  Warning: Firepower scrape failed: {e}")
    return data


def fetch_incarceration_rates():
    """Scrape World Prison Brief data from Wikipedia as a reliable table source."""
    data = {}
    url = "https://en.wikipedia.org/wiki/List_of_countries_by_incarceration_rate"
    try:
        resp = requests.get(url, timeout=30, headers=WIKI_HEADERS)
        resp.raise_for_status()
        soup = BeautifulSoup(resp.text, "html.parser")
        tables = soup.select("table.wikitable")
        if tables:
            rows = tables[0].select("tr")
            for row in rows[1:]:
                cols = row.select("td")
                if len(cols) >= 2:
                    country = cols[0].get_text(strip=True)
                    country = country.split("[")[0].strip()
                    rate = cols[1].get_text(strip=True).replace(",", "")
                    try:
                        data[country] = int(rate)
                    except ValueError:
                        pass
    except Exception as e:
        print(f"  Warning: Incarceration scrape failed: {e}")
    return data


def fetch_corporation_tax():
    """Scrape corporate tax rates from Wikipedia."""
    data = {}
    url = "https://en.wikipedia.org/wiki/List_of_countries_by_tax_rates"
    try:
        resp = requests.get(url, timeout=30, headers=WIKI_HEADERS)
        resp.raise_for_status()
        soup = BeautifulSoup(resp.text, "html.parser")
        tables = soup.select("table.wikitable")
        if tables:
            table = tables[0]
            for row in table.select("tr")[1:]:
                cols = row.select("td")
                # Columns: Country, Corporate, Individual, Capital gains, Wealth, Property
                if len(cols) >= 2:
                    country = cols[0].get_text(strip=True).split("[")[0].strip()
                    tax_text = cols[1].get_text(strip=True)
                    # Extract first number from tax text like "20%[2]"
                    match = re.search(r'(\d+(?:\.\d+)?)', tax_text)
                    if match and country:
                        data[country] = float(match.group(1))
    except Exception as e:
        print(f"  Warning: Corporation tax scrape failed: {e}")
    return data


def fuzzy_lookup(data_dict, country_name):
    """Try to match a country name against keys in a dict."""
    if country_name in data_dict:
        return data_dict[country_name]
    # Try common name variations
    name_lower = country_name.lower()
    for key, value in data_dict.items():
        if key.lower() == name_lower:
            return value
        if name_lower in key.lower() or key.lower() in name_lower:
            return value
    return None


def main():
    print("Fetching data from World Bank API...")

    # World Bank indicators
    print("  GDP per capita...")
    gdp = fetch_world_bank_multi_year("NY.GDP.PCAP.CD")
    time.sleep(0.5)

    print("  Population...")
    population = fetch_world_bank_multi_year("SP.POP.TOTL")
    time.sleep(0.5)

    print("  Gender ratio (male % of population)...")
    male_pct = fetch_world_bank_multi_year("SP.POP.TOTL.MA.ZS")
    time.sleep(0.5)

    print("  Age demographics (% under 14 as proxy, adjusting for under 18)...")
    pop_0_14 = fetch_world_bank_multi_year("SP.POP.0014.TO.ZS")
    pop_15_64 = fetch_world_bank_multi_year("SP.POP.1564.TO.ZS")
    pop_65_up = fetch_world_bank_multi_year("SP.POP.65UP.TO.ZS")
    time.sleep(0.5)

    print("\nScraping additional sources...")
    print("  Firepower index...")
    firepower = fetch_firepower_index()
    time.sleep(1)

    print("  Incarceration rates...")
    incarceration = fetch_incarceration_rates()
    time.sleep(1)

    print("  Corporation tax rates...")
    corp_tax = fetch_corporation_tax()

    # Use World Bank country list as our master list (filter out aggregates)
    # Aggregates tend to have no GDP + population both, or are known groupings
    all_countries = sorted(set(population.keys()) & set(gdp.keys()))

    # Known World Bank aggregate names to exclude
    aggregates = {
        "World", "High income", "Low income", "Lower middle income",
        "Upper middle income", "Middle income", "Low & middle income",
        "East Asia & Pacific", "Europe & Central Asia", "Latin America & Caribbean",
        "Middle East & North Africa", "North America", "South Asia",
        "Sub-Saharan Africa", "Euro area", "European Union",
        "OECD members", "IDA total", "IDA blend", "IDA only",
        "IBRD only", "Fragile and conflict affected situations",
        "Heavily indebted poor countries (HIPC)", "Least developed countries: UN classification",
        "Small states", "Other small states", "Pacific island small states",
        "Caribbean small states", "East Asia & Pacific (excluding high income)",
        "Europe & Central Asia (excluding high income)",
        "Latin America & Caribbean (excluding high income)",
        "Middle East & North Africa (excluding high income)",
        "Sub-Saharan Africa (excluding high income)",
        "Central Europe and the Baltics", "Channel Islands",
        "Early-demographic dividend", "Late-demographic dividend",
        "Post-demographic dividend", "Pre-demographic dividend",
        "Arab World", "IDA & IBRD total",
        "Not classified", "Africa Eastern and Southern", "Africa Western and Central",
        "South Asia (IDA & IBRD)", "East Asia & Pacific (IDA & IBRD countries)",
        "Europe & Central Asia (IDA & IBRD countries)",
        "Latin America & the Caribbean (IDA & IBRD countries)",
        "Middle East & North Africa (IDA & IBRD countries)",
        "Sub-Saharan Africa (IDA & IBRD countries)",
    }

    countries = [c for c in all_countries if c not in aggregates]

    print(f"\nFound {len(countries)} countries. Building CSV...")

    rows = []
    for country in countries:
        # GDP per capita
        gdp_val = gdp.get(country, NA)
        if gdp_val != NA:
            gdp_val = round(float(gdp_val), 2)

        # Population
        pop_val = population.get(country, NA)
        if pop_val != NA:
            pop_val = int(float(pop_val))

        # Gender ratio: convert male % to males per 100 females
        male_pct_val = male_pct.get(country)
        if male_pct_val is not None:
            mp = float(male_pct_val)
            female_pct_val = 100 - mp
            if female_pct_val > 0:
                gender_ratio = round((mp / female_pct_val) * 100, 2)
            else:
                gender_ratio = NA
        else:
            gender_ratio = NA

        # Age: approximate % under 18
        # World Bank gives 0-14 and 15-64. We approximate under-18 as:
        # (0-14%) + ~(4/50 * 15-64%) since 15-17 is about 4 years out of the 50-year 15-64 band
        p014 = pop_0_14.get(country)
        p1564 = pop_15_64.get(country)
        if p014 is not None and p1564 is not None:
            under_18_approx = float(p014) + (4.0 / 50.0) * float(p1564)
            under_18_approx = round(min(under_18_approx, 100), 2)
            over_18_approx = round(100 - under_18_approx, 2)
        else:
            under_18_approx = NA
            over_18_approx = NA

        # Incarceration
        inc_val = fuzzy_lookup(incarceration, country)
        if inc_val is None:
            inc_val = NA

        # Firepower
        fp_val = fuzzy_lookup(firepower, country)
        if fp_val is None:
            fp_val = NA

        # Corporation tax
        ct_val = fuzzy_lookup(corp_tax, country)
        if ct_val is None:
            ct_val = NA

        rows.append([
            country, gdp_val, pop_val, gender_ratio,
            under_18_approx, over_18_approx,
            inc_val, fp_val, ct_val,
        ])

    # Write CSV
    with open(OUTPUT_FILE, "w", newline="", encoding="utf-8") as f:
        writer = csv.writer(f)
        writer.writerow(HEADERS)
        writer.writerows(rows)

    print(f"Done! Wrote {len(rows)} countries to {OUTPUT_FILE}")

    # Summary of coverage
    filled = {h: 0 for h in HEADERS[1:]}
    for row in rows:
        for i, h in enumerate(HEADERS[1:], 1):
            if row[i] != NA:
                filled[h] += 1
    print("\nData coverage:")
    for h, count in filled.items():
        print(f"  {h}: {count}/{len(rows)} countries")


if __name__ == "__main__":
    main()
