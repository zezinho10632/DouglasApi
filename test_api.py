import requests
import json
import uuid
import datetime
import time

BASE_URL = "http://localhost:8080/api/v1"

random_str = str(uuid.uuid4())[:8]
email = f"test_{random_str}@example.com"
password = "Password123!"
name = f"Test User {random_str}"

session = requests.Session()

def register():
    url = f"{BASE_URL}/auth/register"
    data = {
        "name": name,
        "email": email,
        "password": password,
        "role": "ADMIN"
    }
    print(f"Registering user: {email}")
    try:
        res = session.post(url, json=data)
        print(f"Register status: {res.status_code}")
        print(res.text)
    except Exception as e:
        print(f"Connection failed: {e}")

def login():
    url = f"{BASE_URL}/auth/login"
    data = {
        "email": email,
        "password": password
    }
    print("Logging in...")
    try:
        res = session.post(url, json=data)
        print(f"Login status: {res.status_code}")
        if res.status_code == 200:
            token = res.json()['data']['token']
            session.headers.update({'Authorization': f'Bearer {token}'})
            return True
        print(res.text)
    except Exception as e:
        print(f"Connection failed: {e}")
    return False

def get_sector():
    url = f"{BASE_URL}/sectors"
    res = session.get(url)
    if res.status_code == 200:
        sectors = res.json()['data']
        if sectors:
            return sectors[0]['id']
    
    url = f"{BASE_URL}/sectors"
    data = {
        "name": f"Sector {random_str}",
        "code": f"SEC_{random_str}",
        "active": True
    }
    res = session.post(url, json=data)
    if res.status_code == 201:
        return res.json()['data']['id']
    print(f"Failed to create sector: {res.text}")
    return None

def get_classification():
    url = f"{BASE_URL}/notification-classifications"
    res = session.get(url)
    if res.status_code == 200:
        classifications = res.json()['data']
        if classifications:
            return classifications[0]['id']
    
    url = f"{BASE_URL}/notification-classifications"
    data = {"name": f"Class {random_str}"}
    res = session.post(url, json=data)
    if res.status_code == 200:
        return res.json()['data']['id']
    print(f"Failed to create classification: {res.text}")
    return None

def get_period(sector_id):
    now = datetime.datetime.now()
    month = now.month
    year = now.year
    
    url = f"{BASE_URL}/periods"
    data = {
        "sectorId": sector_id,
        "month": month,
        "year": year
    }
    res = session.post(url, json=data)
    if res.status_code == 201:
        return res.json()['data']['id']
    elif res.status_code == 409:
        url = f"{BASE_URL}/periods?sectorId={sector_id}&year={year}"
        res = session.get(url)
        if res.status_code == 200:
            periods = res.json()['data']
            for p in periods:
                if p['month'] == month:
                    return p['id']
    print(f"Failed to create/get period: {res.text}")
    return None

def test_notification(period_id, sector_id, classification_id):
    url = f"{BASE_URL}/notifications"
    # Test 1: Full fields (except professionalCategory)
    data = {
        "periodId": period_id,
        "sectorId": sector_id,
        "classificationId": classification_id,
        "description": "Initial Description",
        "professionalCategoryId": None,
        "quantityClassification": 1,
        "quantityCategory": 1,
        "quantityProfessional": 1,
        "quantity": 1
    }
    print("Creating Notification (With Classification Enum)...")
    res = session.post(url, json=data)
    print(f"Create status: {res.status_code}")
    print(res.text)
    
    if res.status_code == 201:
        notif_id = res.json()['data']['id']
        
        url_update = f"{BASE_URL}/notifications/{notif_id}"
        update_data = {
            "classificationId": classification_id,
            "description": "Updated Description",
            "professionalCategoryId": None,
            "quantityClassification": 2,
            "quantityCategory": 2,
            "quantityProfessional": 2,
            "quantity": 2
        }
        print("Updating Notification...")
        res = session.put(url_update, json=update_data)
        print(f"Update status: {res.status_code}")
        print(res.text)

    # Test 2: Text Classification and Text Professional Category
    data_text = {
        "periodId": period_id,
        "sectorId": sector_id,
        "classificationId": None,
        "classificationText": "Texto Classificação",
        "professionalCategoryText": "Texto Categoria Profissional",
        "description": "Description Text Fields",
        "professionalCategoryId": None,
        "quantityClassification": 5,
        "quantityCategory": 5,
        "quantityProfessional": 5,
        "quantity": 5
    }
    print("Creating Notification (With Text Fields)...")
    res = session.post(url, json=data_text)
    print(f"Create text status: {res.status_code}")
    print(res.text)

    # Test 3: Sector Filter
    print("Testing Sector Filter for Notifications...")
    url_sector = f"{BASE_URL}/notifications?sectorId={sector_id}"
    res = session.get(url_sector)
    print(f"List by Sector status: {res.status_code}")
    if res.status_code == 200:
        count = len(res.json()['data'])
        print(f"Found {count} notifications for sector {sector_id}")
    else:
        print(res.text)

    # Test 4: Sector Filter for Adverse Events (assuming some exist or empty list)
    print("Testing Sector Filter for Adverse Events...")
    url_ae_sector = f"{BASE_URL}/adverse-events/sector/{sector_id}"
    res = session.get(url_ae_sector)
    print(f"List AE by Sector status: {res.status_code}")
    print(res.text)

    # Test 5: Sector Filter for Indicators
    print("Testing Sector Filter for Indicators (Hand Hygiene)...")
    url_hh_sector = f"{BASE_URL}/indicators/hand-hygiene/sector/{sector_id}"
    res = session.get(url_hh_sector)
    print(f"List HH by Sector status: {res.status_code}")
    print(res.text)

    print("Testing Sector Filter for Indicators (Fall Risk)...")
    url_fr_sector = f"{BASE_URL}/indicators/fall-risk/sector/{sector_id}"
    res = session.get(url_fr_sector)
    print(f"List Fall Risk by Sector status: {res.status_code}")
    print(res.text)

    print("Testing Sector Filter for Indicators (Pressure Injury)...")
    url_pi_sector = f"{BASE_URL}/indicators/pressure-injury/sector/{sector_id}"
    res = session.get(url_pi_sector)
    print(f"List Pressure Injury by Sector status: {res.status_code}")
    print(res.text)

    # Test 6: Professional Category Ranking
    print("Testing Professional Category Ranking...")
    url_ranking = f"{BASE_URL}/notifications/ranking/professional-category?sectorId={sector_id}"
    res = session.get(url_ranking)
    print(f"Ranking status: {res.status_code}")
    print(res.text)



if __name__ == "__main__":
    # Wait for server to be up
    print("Waiting for server...")
    for i in range(10):
        try:
            requests.get(f"{BASE_URL}/actuator/health", timeout=1)
            break
        except:
            time.sleep(2)
    
    # 1. Login as Admin
    print("Logging in as Admin...")
    try:
        login_res = session.post(f"{BASE_URL}/auth/login", json={
            "email": "admin@douglas.com",
            "password": "admin123"
        })
        print(f"Login status: {login_res.status_code}")
        if login_res.status_code != 200:
            print(login_res.text)
            exit(1)
            
        token = login_res.json()['data']['token']
        session.headers.update({"Authorization": f"Bearer {token}"})
        
        # 2. Create Sector
        print("Creating Sector...")
        sector_code = f"TEST-{uuid.uuid4().hex[:4]}"
        sector_res = session.post(f"{BASE_URL}/sectors", json={
            "name": f"Test Sector {uuid.uuid4()}",
            "code": sector_code
        })
        if sector_res.status_code == 201:
            sector_id = sector_res.json()['data']['id']
        else:
            # Try to get existing sector if creation fails (though code is random)
            print(f"Sector creation failed: {sector_res.status_code}, {sector_res.text}")
            # Fallback to default sector if exists?
            # Let's assume we can fetch one.
            sectors = session.get(f"{BASE_URL}/sectors").json()['data']
            sector_id = sectors[0]['id']

        # 3. Create Period
        print("Creating Period...")
        # Use next month/year to avoid conflict with default period
        next_month_date = datetime.date.today() + datetime.timedelta(days=32)
        
        period_res = session.post(f"{BASE_URL}/periods", json={
            "sectorId": sector_id,
            "month": next_month_date.month,
            "year": next_month_date.year,
            "status": "OPEN"
        })
        if period_res.status_code == 201:
            period_id = period_res.json()['data']['id']
        else:
            print(f"Period creation failed or exists: {period_res.status_code}")
            # Try to find the period we just tried to create or any open period for this sector
            periods = session.get(f"{BASE_URL}/periods?sectorId={sector_id}").json()['data']
            period_id = periods[0]['id']

        # 4. Create Classification
        print("Creating Classification...")
        class_res = session.post(f"{BASE_URL}/notification-classifications", json={
            "name": f"Test Class {uuid.uuid4()}"
        })
        if class_res.status_code == 200 or class_res.status_code == 201:
            class_id = class_res.json()['data']['id']
        else:
             # Fetch existing
            classes = session.get(f"{BASE_URL}/notification-classifications").json()['data']
            class_id = classes[0]['id']

        # 5. Test Notification
        test_notification(period_id, sector_id, class_id)
        
    except Exception as e:
        print(f"An error occurred: {e}")
