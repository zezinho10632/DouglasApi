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

# --- Auth ---
def login(email, password):
    res = requests.post(f"{BASE_URL}/auth/login", json={"email": email, "password": password})
    if res.status_code == 200:
        token = res.json()['data']['token']
        return token
    return None

def create_manager(admin_token):
    headers = {'Authorization': f'Bearer {admin_token}'}
    manager_email = f"manager_{run_id}@douglas.com"
    manager_pass = "manager123"
    
    # Check if we can register or create via admin
    # Assuming standard register endpoint or admin create
    payload = {
        "email": manager_email,
        "name": f"Manager {run_id}",
        "password": manager_pass,
        "role": "MANAGER",
        "jobTitle": "NURSE" # Assuming NURSE is a valid JobTitle
    }
    
    # Trying register endpoint first (usually public or admin)
    res = requests.post(f"{BASE_URL}/auth/register", json=payload, headers=headers)
    if res.status_code in [200, 201]:
        log(f"Manager created: {manager_email}", "OK")
        return manager_email, manager_pass
    else:
        # If register is not allowed/exists, try login if it was a pre-existing user (unlikely with random ID)
        log(f"Failed to create manager: {res.text}", "WARN")
        return None, None

def test_adverse_event_flow():
    log("\n--- Testing Adverse Event Flow (Admin & Manager) ---")
    
    # 1. Login Admin
    admin_token = login("admin@douglas.com", "admin123")
    if not admin_token:
        log("Admin login failed", "FATAL")
        return

    admin_headers = {'Authorization': f'Bearer {admin_token}'}
    
    # 2. Setup Data (Sector/Period) as Admin
    res = requests.post(f"{BASE_URL}/sectors", json={"name": f"SecAE {run_id}", "code": f"SAE_{run_id}", "active": True}, headers=admin_headers)
    if not check(res, [201], "Admin Create Sector"): return
    sector_id = res.json()['data']['id']
    
    now = datetime.datetime.now()
    res = requests.post(f"{BASE_URL}/periods", json={"sectorId": sector_id, "month": now.month, "year": now.year + 5}, headers=admin_headers)
    if not check(res, [201], "Admin Create Period"): return
    period_id = res.json()['data']['id']

    # 3. Create Adverse Event as Admin (21 Cases, 3 Notifications)
    log("\n--- Admin Action: Create Event ---")
    payload = {
        "periodId": period_id,
        "sectorId": sector_id,
        "eventDate": datetime.date.today().isoformat(),
        "eventType": "FALL",
        "description": "Admin Created Fall",
        "quantityCases": 21,
        "quantityNotifications": 3
    }
    res = requests.post(f"{BASE_URL}/adverse-events", json=payload, headers=admin_headers)
    if check(res, [201], "Admin Create Adverse Event"):
        data = res.json()['data']
        ae_id = data['id']
        if data['quantityCases'] == 21 and data['quantityNotifications'] == 3:
            log("Admin created quantities correct (21/3)", "OK")
        else:
            log(f"Admin created quantities WRONG: {data}", "ERR")

    # 4. Create Manager User
    man_email, man_pass = create_manager(admin_token)
    if not man_email:
        log("Skipping Manager tests due to creation failure", "WARN")
        return

    # 5. Login Manager
    man_token = login(man_email, man_pass)
    if not man_token:
        log("Manager login failed", "FATAL")
        return
    man_headers = {'Authorization': f'Bearer {man_token}'}

    # 6. Manager List/View Event
    log("\n--- Manager Action: View Event ---")
    res = requests.get(f"{BASE_URL}/adverse-events/{ae_id}", headers=man_headers)
    if check(res, [200], "Manager Get Event"):
        data = res.json()['data']
        if data['quantityCases'] == 21 and data['quantityNotifications'] == 3:
            log("Manager sees correct quantities (21/3)", "OK")
        else:
            log(f"Manager sees WRONG quantities: {data}", "ERR")

    # 7. Manager Update Event (if allowed)
    # Checking if Manager can update. Usually yes if they created it or if they have permission.
    # The requirement didn't specify strict ownership for update, but let's try.
    # If Manager can't update Admin's event, we'll try creating one as Manager.
    
    log("\n--- Manager Action: Create Event ---")
    man_payload = {
        "periodId": period_id,
        "sectorId": sector_id,
        "eventDate": datetime.date.today().isoformat(),
        "eventType": "ACCIDENTAL_EXTUBATION",
        "description": "Manager Created Error",
        "quantityCases": 5,
        "quantityNotifications": 5
    }
    res = requests.post(f"{BASE_URL}/adverse-events", json=man_payload, headers=man_headers)
    if check(res, [201], "Manager Create Adverse Event"):
        data = res.json()['data']
        man_ae_id = data['id']
        if data['quantityCases'] == 5 and data['quantityNotifications'] == 5:
            log("Manager created quantities correct (5/5)", "OK")
        else:
            log(f"Manager created quantities WRONG: {data}", "ERR")
            
        # Manager Update their own event
        log("\n--- Manager Action: Update Own Event ---")
        update_payload = {
            "eventDate": datetime.date.today().isoformat(),
            "eventType": "ACCIDENTAL_EXTUBATION",
            "description": "Manager Updated Error",
            "quantityCases": 10,
            "quantityNotifications": 8
        }
        res = requests.put(f"{BASE_URL}/adverse-events/{man_ae_id}", json=update_payload, headers=man_headers)
        if check(res, [200], "Manager Update Event"):
            data = res.json()['data']
            if data['quantityCases'] == 10 and data['quantityNotifications'] == 8:
                log("Manager updated quantities correct (10/8)", "OK")
            else:
                log(f"Manager updated quantities WRONG: {data}", "ERR")

if __name__ == "__main__":
    test_adverse_event_flow()
