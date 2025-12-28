import requests
import json
import uuid
import datetime

BASE_URL = "http://localhost:8080/api/v1"
run_id = str(uuid.uuid4())[:8]
session = requests.Session()

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

def setup_auth():
    admin_email = "admin@douglas.com"
    admin_pass = "admin123"
    res = session.post(f"{BASE_URL}/auth/login", json={"email": admin_email, "password": admin_pass})
    if res.status_code == 200:
        token = res.json()['data']['token']
        session.headers.update({'Authorization': f'Bearer {token}'})
        return True
    return False

def test_adverse_event_quantities():
    log("\n--- Testing Adverse Event Quantities ---")
    
    # 1. Setup Sector/Period
    res = session.post(f"{BASE_URL}/sectors", json={"name": f"SecAE {run_id}", "code": f"SAE_{run_id}", "active": True})
    sector_id = res.json()['data']['id']
    
    now = datetime.datetime.now()
    res = session.post(f"{BASE_URL}/periods", json={"sectorId": sector_id, "month": now.month, "year": now.year + 2}) 
    period_id = res.json()['data']['id']

    # 2. Create Adverse Event with Quantities
    payload = {
        "periodId": period_id,
        "sectorId": sector_id,
        "eventDate": datetime.date.today().isoformat(),
        "eventType": "FALL",
        "description": "Test Fall Event",
        "quantityCases": 21,
        "quantityNotifications": 3
    }
    
    res = session.post(f"{BASE_URL}/adverse-events", json=payload)
    if check(res, [201], "Create Adverse Event with Quantities"):
        data = res.json()['data']
        ae_id = data['id']
        
        # Verify Response
        if data['quantityCases'] == 21 and data['quantityNotifications'] == 3:
            log("Response contains correct quantities", "OK")
        else:
            log(f"Response wrong quantities: {data}", "ERR")
            
        # 3. Update Adverse Event
        update_payload = {
            "eventDate": datetime.date.today().isoformat(),
            "eventType": "FALL",
            "description": "Updated Description",
            "quantityCases": 25,
            "quantityNotifications": 5
        }
        res = session.put(f"{BASE_URL}/adverse-events/{ae_id}", json=update_payload)
        if check(res, [200], "Update Adverse Event"):
            data = res.json()['data']
            if data['quantityCases'] == 25 and data['quantityNotifications'] == 5:
                log("Updated quantities correct", "OK")
            else:
                log(f"Updated quantities wrong: {data}", "ERR")

def run():
    if setup_auth():
        test_adverse_event_quantities()

if __name__ == "__main__":
    run()
