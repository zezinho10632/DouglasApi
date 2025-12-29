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

def test_multiple_open_periods():
    log("--- Testing Multiple Open Periods ---")
    
    # Login
    token = login("admin@douglas.com", "admin123")
    if not token:
        log("Login failed", "FATAL")
        return
    headers = {'Authorization': f'Bearer {token}'}

    # Create Sector
    res = requests.post(f"{BASE_URL}/sectors", json={"name": f"MultiPeriod {run_id}", "code": f"MP_{run_id}", "active": True}, headers=headers)
    if not check(res, [201], "Create Sector"): return
    sector_id = res.json()['data']['id']

    # Create First Open Period (Jan 2030)
    res = requests.post(f"{BASE_URL}/periods", json={"sectorId": sector_id, "month": 1, "year": 2030}, headers=headers)
    if not check(res, [201], "Create First Period (Jan 2030)"): return
    
    # Create Second Open Period (Feb 2030) - Should fail if rule exists, Pass if removed
    res = requests.post(f"{BASE_URL}/periods", json={"sectorId": sector_id, "month": 2, "year": 2030}, headers=headers)
    if check(res, [201], "Create Second Period (Feb 2030)"):
        log("Successfully created second open period!", "SUCCESS")
    else:
        log("Failed to create second open period - Rule still active?", "FAIL")

if __name__ == "__main__":
    test_multiple_open_periods()
