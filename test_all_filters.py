import requests
import json
import uuid
import datetime
import time

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
def setup_auth():
    admin_email = "admin@douglas.com"
    admin_pass = "admin123"
    res = session.post(f"{BASE_URL}/auth/login", json={"email": admin_email, "password": admin_pass})
    if res.status_code == 200:
        token = res.json()['data']['token']
        session.headers.update({'Authorization': f'Bearer {token}'})
        return True
    return False

# --- Global IDs ---
sector_id = None
period_id = None
class_id = None
prof_id = None

def setup_data():
    global sector_id, period_id, class_id, prof_id
    log("\n--- Setup Data ---")
    
    # 1. Sector
    res = session.post(f"{BASE_URL}/sectors", json={"name": f"SecFilter {run_id}", "code": f"SF_{run_id}", "active": True})
    if check(res, [201], "Create Sector"):
        sector_id = res.json()['data']['id']
        
    # 2. Period
    now = datetime.datetime.now()
    res = session.post(f"{BASE_URL}/periods", json={"sectorId": sector_id, "month": now.month, "year": now.year + 1}) # Future year to avoid conflict
    if check(res, [201], "Create Period"):
        period_id = res.json()['data']['id']
        
    # 3. Classification
    res = session.post(f"{BASE_URL}/notification-classifications", json={"name": f"ClassFilter {run_id}"})
    if check(res, [200], "Create Classification"):
        class_id = res.json()['data']['id']
        
    # 4. Professional Category
    res = session.post(f"{BASE_URL}/professional-categories", json={"name": f"ProfFilter {run_id}"})
    if check(res, [200], "Create Prof Category"):
        prof_id = res.json()['data']['id']

    # 5. Create Notification
    data_notif = {
        "periodId": period_id,
        "sectorId": sector_id,
        "classificationId": class_id,
        "professionalCategoryId": prof_id, # If supported in creation? Check DTO if needed. Assuming yes based on flow.
        "description": "Filter Test Notification",
        "quantity": 10,
        "quantityClassification": 5,
        "quantityCategory": 0,
        "quantityProfessional": 5
    }
    # Check if professionalCategoryId is in DTO. If not, it might be inferred or separate.
    # Reading CreateNotificationRequest earlier showed it uses IDs.
    res = session.post(f"{BASE_URL}/notifications", json=data_notif)
    check(res, [201], "Create Notification")

    # 6. Create Indicator (Hand Hygiene)
    res = session.post(f"{BASE_URL}/indicators/hand-hygiene", json={
        "periodId": period_id, "sectorId": sector_id, "compliancePercentage": 80.0
    })
    check(res, [201], "Create Hand Hygiene")

    # 7. Create Adverse Event
    res = session.post(f"{BASE_URL}/adverse-events", json={
        "periodId": period_id, "sectorId": sector_id, 
        "eventDate": datetime.date.today().isoformat(), 
        "eventType": "FALL", "description": "Filter Test AE",
        "quantityCases": 1, "quantityNotifications": 0
    })
    check(res, [201], "Create Adverse Event")
    
    # 8. Create Self Notification
    res = session.post(f"{BASE_URL}/self-notifications", json={
        "periodId": period_id, "sectorId": sector_id, "quantity": 5, "percentage": 10.0
    })
    check(res, [201], "Create Self Notification")

def test_notification_filters():
    log("\n--- Testing Notification Filters ---")
    
    # By Period
    res = session.get(f"{BASE_URL}/notifications", params={"periodId": period_id})
    check(res, [200], f"List Notifs by periodId={period_id}")
    
    # By Sector
    res = session.get(f"{BASE_URL}/notifications", params={"sectorId": sector_id})
    check(res, [200], f"List Notifs by sectorId={sector_id}")
    
    # By Period + Classification
    res = session.get(f"{BASE_URL}/notifications", params={"periodId": period_id, "classificationId": class_id})
    check(res, [200], f"List Notifs by periodId + classificationId")
    
    # Ranking by Period
    res = session.get(f"{BASE_URL}/notifications/ranking/professional-category", params={"periodId": period_id})
    check(res, [200], "Ranking by periodId")
    
    # Ranking by Sector
    res = session.get(f"{BASE_URL}/notifications/ranking/professional-category", params={"sectorId": sector_id})
    check(res, [200], "Ranking by sectorId")

def test_indicator_filters():
    log("\n--- Testing Indicator Filters ---")
    
    # Hand Hygiene By Period (Path)
    res = session.get(f"{BASE_URL}/indicators/hand-hygiene/period/{period_id}")
    check(res, [200], f"Hand Hygiene by periodId")

    # Hand Hygiene By Sector (Path)
    res = session.get(f"{BASE_URL}/indicators/hand-hygiene/sector/{sector_id}")
    check(res, [200], f"Hand Hygiene by sectorId")
    
    # Fall Risk (Check empty/null but 200/404 handling)
    res = session.get(f"{BASE_URL}/indicators/fall-risk/period/{period_id}")
    # Might be 200 with null data or 404 depending on impl. Controller says ApiResponse<FallRiskResponse?>
    check(res, [200, 404], f"Fall Risk by periodId (Empty)")

def test_adverse_event_filters():
    log("\n--- Testing Adverse Event Filters ---")
    
    # By Period (Path)
    res = session.get(f"{BASE_URL}/adverse-events/period/{period_id}")
    check(res, [200], f"Adverse Events by periodId")
    
    # By Period + EventType
    res = session.get(f"{BASE_URL}/adverse-events/period/{period_id}", params={"eventType": "FALL"})
    check(res, [200], f"Adverse Events by periodId + eventType=FALL")
    
    # By Sector (Path)
    res = session.get(f"{BASE_URL}/adverse-events/sector/{sector_id}")
    check(res, [200], f"Adverse Events by sectorId")

def test_period_filters():
    log("\n--- Testing Period Filters ---")
    
    # By Sector (Required)
    res = session.get(f"{BASE_URL}/periods", params={"sectorId": sector_id})
    check(res, [200], f"Periods by sectorId")
    
    # By Sector + Status
    res = session.get(f"{BASE_URL}/periods", params={"sectorId": sector_id, "status": "OPEN"})
    check(res, [200], f"Periods by sectorId + status=OPEN")

def test_report_filters():
    log("\n--- Testing Report Filters ---")
    
    # Panel by Period + Sector
    res = session.get(f"{BASE_URL}/reports/panel", params={"periodId": period_id, "sectorId": sector_id})
    check(res, [200], f"Report Panel by periodId + sectorId")
    
    # Cumulative by Sector (Needs dates for CUSTOM default)
    now = datetime.datetime.now()
    start_date = datetime.date(now.year, 1, 1).isoformat()
    end_date = datetime.date(now.year, 12, 31).isoformat()
    
    res = session.get(f"{BASE_URL}/reports/panel/cumulative", params={
        "sectorId": sector_id, 
        "startDate": start_date, 
        "endDate": end_date
    })
    check(res, [200], f"Report Cumulative by sectorId (Custom Dates)")

def test_self_notification_filters():
    log("\n--- Testing Self Notification Filters ---")
    
    # By Period (Path)
    res = session.get(f"{BASE_URL}/self-notifications/period/{period_id}")
    check(res, [200], f"Self Notif by periodId")

def run():
    if setup_auth():
        try:
            setup_data()
            if period_id and sector_id:
                test_notification_filters()
                test_indicator_filters()
                test_adverse_event_filters()
                test_period_filters()
                test_report_filters()
                test_self_notification_filters()
            else:
                log("Setup failed, skipping tests", "WARN")
        except Exception as e:
            log(f"Exception: {e}", "FATAL")

if __name__ == "__main__":
    run()
