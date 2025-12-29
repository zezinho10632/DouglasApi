import requests
import json
import uuid
import datetime

BASE_URL = "http://localhost:8080/api/v1"
run_id = str(uuid.uuid4())[:8]

def log(msg, status=None):
    if status:
        print(f"[{status}] {msg}")
    else:
        print(f"{msg}")

def check(response, expected_codes=[200, 201], msg=""):
    if response.status_code in expected_codes:
        log(f"PASS: {msg} ({response.status_code})", "OK")
        return True
    else:
        log(f"FAIL: {msg} ({response.status_code}) - {response.text}", "ERR")
        return False

def login(email, password):
    res = requests.post(f"{BASE_URL}/auth/login", json={"email": email, "password": password})
    if res.status_code == 200:
        return res.json()['data']['token']
    return None

def test_pressure_injury_very_high():
    log("--- Testing Pressure Injury 'Very High' Field ---")
    
    # Login
    token = login("admin@douglas.com", "admin123")
    if not token:
        log("Login failed", "FATAL")
        return
    headers = {'Authorization': f'Bearer {token}'}

    # Create Sector
    res = requests.post(f"{BASE_URL}/sectors", json={"name": f"PI Sector {run_id}", "code": f"PI_{run_id}", "active": True}, headers=headers)
    if not check(res, [201], "Create Sector"): return
    sector_id = res.json()['data']['id']

    # Create Period
    res = requests.post(f"{BASE_URL}/periods", json={"sectorId": sector_id, "month": 6, "year": 2035}, headers=headers)
    if not check(res, [201], "Create Period"): return
    period_id = res.json()['data']['id']

    # Create Pressure Injury Assessment with Very High
    payload = {
        "periodId": period_id,
        "sectorId": sector_id,
        "totalPatients": 100,
        "assessedOnAdmission": 100,
        "veryHigh": 10,
        "highRisk": 20,
        "mediumRisk": 30,
        "lowRisk": 30,
        "notAssessed": 10
    }
    
    res = requests.post(f"{BASE_URL}/indicators/pressure-injury", json=payload, headers=headers)
    if check(res, [201], "Create Pressure Injury Assessment"):
        data = res.json()['data']
        if data['veryHigh'] == 10 and data['veryHighPercentage'] == 10.0: # 10/100 * 100
             log("Correctly saved 'veryHigh' = 10 and calculated 'veryHighPercentage' = 10.00", "SUCCESS")
        else:
             log(f"Incorrect values: veryHigh={data.get('veryHigh')}, veryHighPercentage={data.get('veryHighPercentage')}", "FAIL")

    # Update Pressure Injury Assessment
    pi_id = res.json()['data']['id']
    update_payload = {
        "periodId": period_id, # Usually redundant for update but included in DTO
        "sectorId": sector_id,
        "totalPatients": 200,
        "assessedOnAdmission": 200,
        "veryHigh": 40, # 20%
        "highRisk": 40,
        "mediumRisk": 40,
        "lowRisk": 40,
        "notAssessed": 40
    }
    
    res = requests.put(f"{BASE_URL}/indicators/pressure-injury/{pi_id}", json=update_payload, headers=headers)
    if check(res, [200], "Update Pressure Injury Assessment"):
        data = res.json()['data']
        if data['veryHigh'] == 40 and data['veryHighPercentage'] == 20.0:
             log("Correctly updated 'veryHigh' = 40 and calculated 'veryHighPercentage' = 20.00", "SUCCESS")
        else:
             log(f"Incorrect updated values: veryHigh={data.get('veryHigh')}, veryHighPercentage={data.get('veryHighPercentage')}", "FAIL")

if __name__ == "__main__":
    test_pressure_injury_very_high()
