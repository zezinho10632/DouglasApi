import requests
import json
import uuid
import datetime
import time

BASE_URL = "http://localhost:8080/api/v1"

# Generate unique identifiers for this run
run_id = str(uuid.uuid4())[:8]
email = f"admin_{run_id}@test.com"
password = "Password123!"
name = f"Admin {run_id}"

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
def setup_auth():
    log("--- Auth Setup ---")
    
    # Try logging in as default admin first to create new users
    admin_email = "admin@douglas.com"
    admin_pass = "admin123"
    
    res = session.post(f"{BASE_URL}/auth/login", json={"email": admin_email, "password": admin_pass})
    
    if res.status_code == 200:
        log("Logged in as Default Admin", "INFO")
        token = res.json()['data']['token']
        session.headers.update({'Authorization': f'Bearer {token}'})
        
        # Now register the test user
        res = session.post(f"{BASE_URL}/auth/register", json={
            "name": name, "email": email, "password": password, "role": "ADMIN", "jobTitle": "ADMIN"
        })
        if check(res, [201], "Register Test User"):
             # Login as the new test user to keep isolation
             res_login = session.post(f"{BASE_URL}/auth/login", json={"email": email, "password": password})
             if check(res_login, [200], "Login as Test User"):
                 token = res_login.json()['data']['token']
                 session.headers.update({'Authorization': f'Bearer {token}'})
                 return True
    else:
        log("Failed to login as Default Admin. Trying direct registration...", "WARN")
        # Fallback (original logic)
        res = session.post(f"{BASE_URL}/auth/register", json={
            "name": name, "email": email, "password": password, "role": "ADMIN", "jobTitle": "ADMIN"
        })
        check(res, [201], "Register User")

        # Login
        res = session.post(f"{BASE_URL}/auth/login", json={"email": email, "password": password})
        if check(res, [200], "Login"):
            token = res.json()['data']['token']
            session.headers.update({'Authorization': f'Bearer {token}'})
            return True
            
    return False

# --- Global IDs ---
sector_id = None
period_id = None
prof_cat_id = None
class_id = None

# --- Sector ---
def test_sector():
    global sector_id
    log("\n--- Testing Sector ---")
    # Create
    data = {"name": f"Sector {run_id}", "code": f"SEC_{run_id}", "active": True}
    res = session.post(f"{BASE_URL}/sectors", json=data)
    if check(res, [201], "Create Sector"):
        sector_id = res.json()['data']['id']
    
    # Update
    if sector_id:
        res = session.put(f"{BASE_URL}/sectors/{sector_id}", json={"name": f"Sector Updated {run_id}", "active": True})
        check(res, [200], "Update Sector")
        
        # Get By ID
        res = session.get(f"{BASE_URL}/sectors/{sector_id}")
        check(res, [200], "Get Sector By ID")

        # List Active
        res = session.get(f"{BASE_URL}/sectors")
        check(res, [200], "List Active Sectors")

# --- Period ---
def test_period():
    global period_id
    log("\n--- Testing Period ---")
    if not sector_id:
        log("SKIP: No Sector ID", "WARN")
        return

    now = datetime.datetime.now()
    month = now.month
    year = now.year

    # Create (Check if exists first to avoid 409)
    res = session.get(f"{BASE_URL}/periods?sectorId={sector_id}&year={year}")
    existing = False
    if res.status_code == 200:
        periods = res.json()['data']
        for p in periods:
            if p['month'] == month:
                period_id = p['id']
                existing = True
                log("Period already exists, using existing ID.", "INFO")
                break
    
    if not existing:
        data = {"sectorId": sector_id, "month": month, "year": year}
        res = session.post(f"{BASE_URL}/periods", json=data)
        if check(res, [201], "Create Period"):
            period_id = res.json()['data']['id']

    # List by Sector
    res = session.get(f"{BASE_URL}/periods?sectorId={sector_id}")
    check(res, [200], "List Periods by Sector")

    # Close (Optional, might block other tests if strict, so skipping or reopening immediately)
    # Reopen

# --- Professional Category ---
def test_professional_category():
    global prof_cat_id
    log("\n--- Testing Professional Category ---")
    # Create
    data = {"name": f"ProfCat {run_id}"}
    res = session.post(f"{BASE_URL}/professional-categories", json=data)
    if check(res, [200], "Create Prof Category"):
        prof_cat_id = res.json()['data']['id']
    
    # Update
    if prof_cat_id:
        res = session.put(f"{BASE_URL}/professional-categories/{prof_cat_id}", json={"name": f"ProfCat Upd {run_id}", "active": True})
        check(res, [200], "Update Prof Category")

        # List
        res = session.get(f"{BASE_URL}/professional-categories")
        check(res, [200], "List Prof Categories")

# --- Notification Classification ---
def test_classification():
    global class_id
    log("\n--- Testing Notification Classification ---")
    # Create
    data = {"name": f"Class {run_id}"}
    res = session.post(f"{BASE_URL}/notification-classifications", json=data)
    if check(res, [200], "Create Classification"):
        class_id = res.json()['data']['id']
    
    # Update
    if class_id:
        res = session.put(f"{BASE_URL}/notification-classifications/{class_id}", json={"name": f"Class Upd {run_id}", "active": True})
        check(res, [200], "Update Classification")

        # List
        res = session.get(f"{BASE_URL}/notification-classifications")
        check(res, [200], "List Classifications")

# --- Notifications ---
def test_notifications():
    log("\n--- Testing Notifications ---")
    if not (period_id and sector_id):
        log("SKIP: Missing IDs", "WARN")
        return

    notif_id = None
    # Create (Enum + Quantity)
    data = {
        "periodId": period_id,
        "sectorId": sector_id,
        "classificationId": class_id,
        "description": "Test Notif Enum",
        "quantity": 10,
        "quantityClassification": 5,
        "quantityCategory": 0,
        "quantityProfessional": 5
    }
    res = session.post(f"{BASE_URL}/notifications", json=data)
    if check(res, [201], "Create Notification (Enum)"):
        notif_id = res.json()['data']['id']

    # Update
    if notif_id:
        data_upd = {
            "classificationId": class_id,
            "description": "Test Notif Updated",
            "quantity": 20,
            "quantityClassification": 10,
            "quantityCategory": 0,
            "quantityProfessional": 10
        }
        res = session.put(f"{BASE_URL}/notifications/{notif_id}", json=data_upd)
        check(res, [200], "Update Notification")

        # Get By ID
        res = session.get(f"{BASE_URL}/notifications/{notif_id}")
        check(res, [200], "Get Notification By ID")

    # List by Sector
    res = session.get(f"{BASE_URL}/notifications?sectorId={sector_id}")
    check(res, [200], "List Notifications by Sector")

    # Ranking
    res = session.get(f"{BASE_URL}/notifications/ranking/professional-category?periodId={period_id}")
    check(res, [200], "Get Ranking")

# --- Indicators ---
def test_indicators():
    log("\n--- Testing Indicators ---")
    if not (period_id and sector_id):
        log("SKIP: Missing IDs", "WARN")
        return

    # 1. Hand Hygiene
    hh_id = None
    data_hh = {"periodId": period_id, "sectorId": sector_id, "compliancePercentage": 85.5}
    res = session.post(f"{BASE_URL}/indicators/hand-hygiene", json=data_hh)
    # Might conflict if already exists (mock data), so handle 409 or 201
    if res.status_code == 409:
        log("Hand Hygiene already exists, trying to fetch via List", "INFO")
        res_list = session.get(f"{BASE_URL}/indicators/hand-hygiene/period/{periodId}")
        if res_list.status_code == 200 and res_list.json()['data']:
            hh_id = res_list.json()['data']['id']
    elif check(res, [201], "Create Hand Hygiene"):
        hh_id = res.json()['data']['id']
    
    if hh_id:
        res = session.put(f"{BASE_URL}/indicators/hand-hygiene/{hh_id}", json={"periodId": period_id, "sectorId": sector_id, "compliancePercentage": 90.0})
        check(res, [200], "Update Hand Hygiene")
        
        res = session.get(f"{BASE_URL}/indicators/hand-hygiene/{hh_id}")
        check(res, [200], "Get Hand Hygiene By ID")

    # 2. Fall Risk
    fr_id = None
    data_fr = {
        "periodId": period_id, "sectorId": sector_id, 
        "totalPatients": 100, "assessedOnAdmission": 90, 
        "highRisk": 10, "mediumRisk": 30, "lowRisk": 50, "notAssessed": 10
    }
    res = session.post(f"{BASE_URL}/indicators/fall-risk", json=data_fr)
    if res.status_code == 409:
         # Fetch existing if needed, skipping for brevity as pattern is same
         log("Fall Risk already exists", "INFO")
    else:
        check(res, [201], "Create Fall Risk")
        fr_id = res.json()['data']['id'] if res.status_code == 201 else None

    # 3. Pressure Injury
    pi_id = None
    data_pi = {
        "periodId": period_id, "sectorId": sector_id, 
        "totalPatients": 100, "assessedOnAdmission": 95, 
        "highRisk": 5, "mediumRisk": 20, "lowRisk": 70, "notAssessed": 5
    }
    res = session.post(f"{BASE_URL}/indicators/pressure-injury", json=data_pi)
    if res.status_code != 409:
        check(res, [201], "Create Pressure Injury")
        pi_id = res.json()['data']['id'] if res.status_code == 201 else None

    # 4. Meta Compliance
    meta_id = None
    data_meta = {"periodId": period_id, "sectorId": sector_id, "goalValue": 100, "percentage": 95}
    res = session.post(f"{BASE_URL}/indicators/meta-compliance", json=data_meta)
    if res.status_code != 409:
        check(res, [201], "Create Meta Compliance")
        meta_id = res.json()['data']['id'] if res.status_code == 201 else None
    
    # 5. Medication Compliance
    med_id = None
    data_med = {"periodId": period_id, "sectorId": sector_id, "percentage": 99.9}
    res = session.post(f"{BASE_URL}/indicators/medication-compliance", json=data_med)
    if res.status_code != 409:
        check(res, [201], "Create Medication Compliance")
        med_id = res.json()['data']['id'] if res.status_code == 201 else None

    # 6. Self Notification
    self_id = None
    data_self = {"periodId": period_id, "sectorId": sector_id, "quantity": 15, "percentage": 50.0}
    res = session.post(f"{BASE_URL}/self-notifications", json=data_self)
    if res.status_code != 409:
        check(res, [201], "Create Self Notification")
        self_id = res.json()['data']['id'] if res.status_code == 201 else None

# --- Adverse Events ---
def test_adverse_events():
    log("\n--- Testing Adverse Events ---")
    if not (period_id and sector_id):
        return
    
    ae_id = None
    data = {
        "periodId": period_id, "sectorId": sector_id, 
        "eventDate": datetime.date.today().isoformat(), 
        "eventType": "FALL", "description": "Patient fell"
    }
    res = session.post(f"{BASE_URL}/adverse-events", json=data)
    if check(res, [201], "Create Adverse Event"):
        ae_id = res.json()['data']['id']
    
    if ae_id:
        res = session.get(f"{BASE_URL}/adverse-events/{ae_id}")
        check(res, [200], "Get Adverse Event By ID")
        
        # List by Sector
        res = session.get(f"{BASE_URL}/adverse-events/sector/{sector_id}")
        check(res, [200], "List Adverse Events by Sector")

# --- Users ---
def test_users():
    log("\n--- Testing Users ---")
    res = session.get(f"{BASE_URL}/users")
    check(res, [200], "List Users")

# --- Audit Logs ---
def test_audit_logs():
    log("\n--- Testing Audit Logs ---")
    res = session.get(f"{BASE_URL}/audit-logs")
    check(res, [200], "Search Audit Logs")

# --- Reports ---
def test_reports():
    log("\n--- Testing Reports ---")
    if not (period_id and sector_id):
        return
    
    # Complete Panel
    res = session.get(f"{BASE_URL}/reports/panel?periodId={period_id}&sectorId={sector_id}")
    check(res, [200], "Generate Complete Panel Report")

def run_all():
    if not setup_auth():
        log("Auth failed, aborting.", "FATAL")
        return

    test_sector()
    test_period()
    test_professional_category()
    test_classification()
    test_notifications()
    test_indicators()
    test_adverse_events()
    test_users()
    test_audit_logs()
    test_reports()

    # Cleanup (Optional - Delete Sector which cascades?)
    # if sector_id:
    #     session.delete(f"{BASE_URL}/sectors/{sector_id}")

if __name__ == "__main__":
    run_all()
