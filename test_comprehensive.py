import requests
import json
import uuid

BASE_URL = "http://localhost:8080/api/v1"
EMAIL = "admin@hospital.com"
PASSWORD = "admin"

session = requests.Session()
token = None

def login():
    global token
    url = f"{BASE_URL}/auth/login"
    payload = {
        "email": EMAIL,
        "password": PASSWORD
    }
    try:
        response = session.post(url, json=payload)
        print(f"Login status: {response.status_code}")
        if response.status_code == 200:
            token = response.json()["data"]["token"]
            session.headers.update({"Authorization": f"Bearer {token}"})
            print("Logged in successfully.")
            return True
        else:
            print(f"Login failed: {response.text}")
            return False
    except Exception as e:
        print(f"Login error: {e}")
        return False

def test_crud(entity_name, base_endpoint, create_payload, update_payload):
    print(f"\n--- Testing {entity_name} ---")
    
    # 1. Create
    print(f"Creating {entity_name}...")
    create_response = session.post(f"{BASE_URL}/{base_endpoint}", json=create_payload)
    print(f"Create status: {create_response.status_code}")
    if create_response.status_code not in [200, 201]:
        print(f"Create failed: {create_response.text}")
        return None
    
    data = create_response.json()["data"]
    entity_id = data["id"]
    print(f"{entity_name} created with ID: {entity_id}")
    
    # 2. Get/List (Generic check if list exists)
    print(f"Listing {entity_name}s...")
    list_response = session.get(f"{BASE_URL}/{base_endpoint}")
    if list_response.status_code == 200:
        print("List success.")
    else:
        # Some endpoints might not have a simple list all, or require params
        print(f"List status: {list_response.status_code} (Might be expected if params required)")

    # 3. Update
    print(f"Updating {entity_name}...")
    update_response = session.put(f"{BASE_URL}/{base_endpoint}/{entity_id}", json=update_payload)
    print(f"Update status: {update_response.status_code}")
    if update_response.status_code != 200:
        print(f"Update failed: {update_response.text}")
    
    # 4. Get by ID
    print(f"Getting {entity_name} by ID...")
    get_response = session.get(f"{BASE_URL}/{base_endpoint}/{entity_id}")
    if get_response.status_code == 200:
        print("Get by ID success.")
        # Verify update
        # This part depends on the structure, simplifying for generic test
    else:
        print(f"Get by ID failed: {get_response.text}")

    return entity_id

def delete_entity(entity_name, base_endpoint, entity_id):
    print(f"Deleting {entity_name} ({entity_id})...")
    delete_response = session.delete(f"{BASE_URL}/{base_endpoint}/{entity_id}")
    print(f"Delete status: {delete_response.status_code}")
    if delete_response.status_code == 200:
        print(f"{entity_name} deleted successfully.")
    else:
        print(f"Delete failed: {delete_response.text}")

def run_tests():
    if not login():
        return

    # --- Auxiliary Entities ---
    
    # Sector
    sector_payload = {"name": f"Test Sector {uuid.uuid4()}", "active": True}
    sector_update = {"name": f"Updated Sector {uuid.uuid4()}", "active": True}
    sector_id = test_crud("Sector", "sectors", sector_payload, sector_update)

    # Period
    period_payload = {"name": f"Test Period {uuid.uuid4()}", "startDate": "2025-01-01", "endDate": "2025-01-31", "status": "OPEN"}
    period_update = {"name": f"Updated Period {uuid.uuid4()}", "startDate": "2025-01-01", "endDate": "2025-01-31", "status": "OPEN"}
    period_id = test_crud("Period", "periods", period_payload, period_update)

    if not sector_id or not period_id:
        print("Critical dependencies missing. Stopping.")
        return

    # Notification Classification
    class_payload = {"name": f"Class {uuid.uuid4()}", "active": True}
    class_update = {"name": f"Upd Class {uuid.uuid4()}", "active": True}
    class_id = test_crud("Notification Classification", "notification-classifications", class_payload, class_update)
    # Don't delete yet, needed for notification

    # Professional Category
    prof_payload = {"name": f"Prof Cat {uuid.uuid4()}", "active": True}
    prof_update = {"name": f"Upd Prof {uuid.uuid4()}", "active": True}
    prof_id = test_crud("Professional Category", "professional-categories", prof_payload, prof_update)
    # Don't delete yet

    # --- Core Entities ---

    # Notification (Enum Based)
    notif_payload = {
        "periodId": period_id,
        "sectorId": sector_id,
        "classificationId": class_id,
        "description": "Enum Description",
        "professionalCategoryId": prof_id,
        "quantityClassification": 1,
        "quantityCategory": 1,
        "quantityProfessional": 1,
        "quantity": 1
    }
    notif_update = {
        "classificationId": class_id,
        "description": "Updated Enum Description",
        "professionalCategoryId": prof_id,
        "quantityClassification": 2,
        "quantityCategory": 2,
        "quantityProfessional": 2,
        "quantity": 2,
        "classificationText": None,
        "professionalCategoryText": None
    }
    notif_id = test_crud("Notification (Enum)", "notifications", notif_payload, notif_update)
    
    # Notification (Text Based)
    notif_text_payload = {
        "periodId": period_id,
        "sectorId": sector_id,
        "classificationText": "Text Class",
        "description": "Text Description",
        "professionalCategoryText": "Text Prof",
        "quantityClassification": 5,
        "quantityCategory": 5,
        "quantityProfessional": 5,
        "quantity": 5
    }
    print("\n--- Testing Notification (Text) ---")
    create_text_resp = session.post(f"{BASE_URL}/notifications", json=notif_text_payload)
    print(f"Create Text status: {create_text_resp.status_code}")
    if create_text_resp.status_code == 201:
        notif_text_id = create_text_resp.json()["data"]["id"]
        delete_entity("Notification (Text)", "notifications", notif_text_id)

    # Ranking
    print("\nTesting Professional Ranking...")
    ranking_resp = session.get(f"{BASE_URL}/notifications/ranking/professional-category?periodId={period_id}")
    print(f"Ranking status: {ranking_resp.status_code}")
    
    # Delete Notification (Enum)
    if notif_id:
        delete_entity("Notification (Enum)", "notifications", notif_id)

    # Clean up Classification/Category
    if class_id: delete_entity("Notification Classification", "notification-classifications", class_id)
    if prof_id: delete_entity("Professional Category", "professional-categories", prof_id)


    # Adverse Event
    ae_payload = {
        "periodId": period_id,
        "sectorId": sector_id,
        "eventDate": "2025-01-15T10:00:00",
        "description": "Test Event",
        "type": "FALL",
        "severity": "LOW",
        "origin": "Test Origin"
    }
    ae_update = ae_payload.copy()
    ae_update["description"] = "Updated Event"
    ae_id = test_crud("Adverse Event", "adverse-events", ae_payload, ae_update)
    if ae_id: delete_entity("Adverse Event", "adverse-events", ae_id)


    # --- Indicators ---

    # Compliance
    comp_payload = {
        "periodId": period_id,
        "sectorId": sector_id,
        "completeWristband": 90.5,
        "patientCommunication": 80.0,
        "medicationIdentified": 95.0,
        "handHygieneAdherence": 85.0,
        "fallRiskAssessment": 99.0,
        "pressureInjuryRiskAssessment": 98.0,
        "observations": "Obs"
    }
    comp_update = comp_payload.copy()
    comp_update["observations"] = "Updated Obs"
    
    # Specific path for indicators
    print("\n--- Testing Compliance Indicator ---")
    resp = session.post(f"{BASE_URL}/indicators/compliance", json=comp_payload)
    print(f"Create status: {resp.status_code}")
    if resp.status_code == 201:
        c_id = resp.json()["data"]["id"]
        session.put(f"{BASE_URL}/indicators/compliance/{c_id}", json=comp_update)
        session.get(f"{BASE_URL}/indicators/compliance/{c_id}")
        session.delete(f"{BASE_URL}/indicators/compliance/{c_id}")
        print("Compliance CRUD success.")

    # Hand Hygiene
    hh_payload = {
        "periodId": period_id,
        "sectorId": sector_id,
        "compliancePercentage": 88.8
    }
    print("\n--- Testing Hand Hygiene ---")
    resp = session.post(f"{BASE_URL}/indicators/hand-hygiene", json=hh_payload)
    print(f"Create status: {resp.status_code}")
    if resp.status_code == 201:
        h_id = resp.json()["data"]["id"]
        hh_payload["compliancePercentage"] = 99.9
        session.put(f"{BASE_URL}/indicators/hand-hygiene/{h_id}", json=hh_payload)
        session.delete(f"{BASE_URL}/indicators/hand-hygiene/{h_id}")
        print("Hand Hygiene CRUD success.")

    # Fall Risk
    fr_payload = {
        "periodId": period_id,
        "sectorId": sector_id,
        "totalPatients": 100,
        "assessedOnAdmission": 90,
        "highRisk": 10,
        "mediumRisk": 20,
        "lowRisk": 60,
        "notAssessed": 10
    }
    print("\n--- Testing Fall Risk ---")
    resp = session.post(f"{BASE_URL}/indicators/fall-risk", json=fr_payload)
    print(f"Create status: {resp.status_code}")
    if resp.status_code == 201:
        f_id = resp.json()["data"]["id"]
        fr_payload["totalPatients"] = 101
        session.put(f"{BASE_URL}/indicators/fall-risk/{f_id}", json=fr_payload)
        session.delete(f"{BASE_URL}/indicators/fall-risk/{f_id}")
        print("Fall Risk CRUD success.")

    # Pressure Injury
    pi_payload = {
        "periodId": period_id,
        "sectorId": sector_id,
        "totalPatients": 50,
        "assessedOnAdmission": 45,
        "highRisk": 5,
        "mediumRisk": 10,
        "lowRisk": 30,
        "notAssessed": 5
    }
    print("\n--- Testing Pressure Injury ---")
    resp = session.post(f"{BASE_URL}/indicators/pressure-injury", json=pi_payload)
    print(f"Create status: {resp.status_code}")
    if resp.status_code == 201:
        p_id = resp.json()["data"]["id"]
        pi_payload["totalPatients"] = 51
        session.put(f"{BASE_URL}/indicators/pressure-injury/{p_id}", json=pi_payload)
        session.delete(f"{BASE_URL}/indicators/pressure-injury/{p_id}")
        print("Pressure Injury CRUD success.")

    # Meta Compliance
    meta_payload = {
        "periodId": period_id,
        "sectorId": sector_id,
        "goalValue": 95.0,
        "percentage": 90.0
    }
    print("\n--- Testing Meta Compliance ---")
    resp = session.post(f"{BASE_URL}/indicators/meta-compliance", json=meta_payload)
    print(f"Create status: {resp.status_code}")
    if resp.status_code == 201:
        m_id = resp.json()["data"]["id"]
        meta_payload["percentage"] = 92.0
        session.put(f"{BASE_URL}/indicators/meta-compliance/{m_id}", json=meta_payload)
        session.delete(f"{BASE_URL}/indicators/meta-compliance/{m_id}")
        print("Meta Compliance CRUD success.")

    # Medication Compliance
    med_payload = {
        "periodId": period_id,
        "sectorId": sector_id,
        "percentage": 98.5
    }
    print("\n--- Testing Medication Compliance ---")
    resp = session.post(f"{BASE_URL}/indicators/medication-compliance", json=med_payload)
    print(f"Create status: {resp.status_code}")
    if resp.status_code == 201:
        med_id = resp.json()["data"]["id"]
        med_payload["percentage"] = 99.0
        session.put(f"{BASE_URL}/indicators/medication-compliance/{med_id}", json=med_payload)
        session.delete(f"{BASE_URL}/indicators/medication-compliance/{med_id}")
        print("Medication Compliance CRUD success.")


    # Clean up Sector/Period
    delete_entity("Sector", "sectors", sector_id)
    delete_entity("Period", "periods", period_id)

    print("\n--- ALL TESTS COMPLETED ---")

if __name__ == "__main__":
    run_tests()
