import requests
import json
import uuid
import time

BASE_URL = "http://localhost:8080/api/v1"

# Generate unique run ID
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
    log("--- Auth Setup ---")
    
    # Login as default admin
    admin_email = "admin@douglas.com"
    admin_pass = "admin123"
    
    res = session.post(f"{BASE_URL}/auth/login", json={"email": admin_email, "password": admin_pass})
    
    if res.status_code == 200:
        log("Logged in as Default Admin", "INFO")
        token = res.json()['data']['token']
        session.headers.update({'Authorization': f'Bearer {token}'})
        return True
    else:
        log(f"Failed to login as Default Admin: {res.status_code}", "FATAL")
        return False

# --- Setup Test Users ---
users = [
    {"name": f"Alice FilterTest {run_id}", "email": f"alice_{run_id}@test.com", "password": "Password123!", "role": "ADMIN", "jobTitle": "ADMIN"},
    {"name": f"Bob FilterTest {run_id}", "email": f"bob_{run_id}@test.com", "password": "Password123!", "role": "MANAGER", "jobTitle": "NURSE"},
    {"name": f"Charlie FilterTest {run_id}", "email": f"charlie_{run_id}@other.com", "password": "Password123!", "role": "MANAGER", "jobTitle": "DOCTOR"}
]

def create_test_users():
    log("\n--- Creating Test Users ---")
    for u in users:
        res = session.post(f"{BASE_URL}/auth/register", json=u)
        check(res, [201], f"Register {u['name']}")

def test_filters():
    log("\n--- Testing Filters ---")
    
    # 1. Filter by Name "Alice"
    log("Test 1: Filter by Name (Alice)")
    res = session.get(f"{BASE_URL}/users", params={"name": "Alice"})
    if check(res, [200], "Filter by Name"):
        data = res.json()['data']
        found = any(u['email'] == users[0]['email'] for u in data)
        log(f"Found Alice: {found}", "OK" if found else "ERR")

    # 2. Filter by Email Domain "test.com"
    log("Test 2: Filter by Email (test.com)")
    res = session.get(f"{BASE_URL}/users", params={"email": "test.com"})
    if check(res, [200], "Filter by Email"):
        data = res.json()['data']
        found_alice = any(u['email'] == users[0]['email'] for u in data)
        found_bob = any(u['email'] == users[1]['email'] for u in data)
        found_charlie = any(u['email'] == users[2]['email'] for u in data)
        
        if found_alice and found_bob and not found_charlie:
            log("Found Alice and Bob, NOT Charlie: YES", "OK")
        else:
            log(f"Result mismatch: Alice={found_alice}, Bob={found_bob}, Charlie={found_charlie}", "ERR")

    # 3. Filter by Role "MANAGER"
    log("Test 3: Filter by Role (MANAGER)")
    res = session.get(f"{BASE_URL}/users", params={"role": "MANAGER"})
    if check(res, [200], "Filter by Role"):
        data = res.json()['data']
        # Should find Bob and Charlie, but NOT Alice
        found_alice = any(u['email'] == users[0]['email'] for u in data)
        found_bob = any(u['email'] == users[1]['email'] for u in data)
        found_charlie = any(u['email'] == users[2]['email'] for u in data)
        
        if not found_alice and found_bob and found_charlie:
            log("Found Bob and Charlie, NOT Alice: YES", "OK")
        else:
            log(f"Result mismatch: Alice={found_alice}, Bob={found_bob}, Charlie={found_charlie}", "ERR")

    # 4. Filter by JobTitle "DOCTOR"
    log("Test 4: Filter by JobTitle (DOCTOR)")
    res = session.get(f"{BASE_URL}/users", params={"jobTitle": "DOCTOR"})
    if check(res, [200], "Filter by JobTitle"):
        data = res.json()['data']
        found_charlie = any(u['email'] == users[2]['email'] for u in data)
        found_bob = any(u['email'] == users[1]['email'] for u in data)
        
        if found_charlie and not found_bob:
            log("Found Charlie, NOT Bob: YES", "OK")
        else:
            log(f"Result mismatch: Charlie={found_charlie}, Bob={found_bob}", "ERR")
            
    # 5. Combined Filter: Role MANAGER + Email "other.com"
    log("Test 5: Combined Filter (Role MANAGER + Email other.com)")
    res = session.get(f"{BASE_URL}/users", params={"role": "MANAGER", "email": "other.com"})
    if check(res, [200], "Combined Filter"):
        data = res.json()['data']
        found_charlie = any(u['email'] == users[2]['email'] for u in data)
        found_bob = any(u['email'] == users[1]['email'] for u in data)
        
        if found_charlie and not found_bob:
            log("Found Charlie only: YES", "OK")
        else:
            log(f"Result mismatch: Charlie={found_charlie}, Bob={found_bob}", "ERR")

def run():
    if setup_auth():
        create_test_users()
        test_filters()

if __name__ == "__main__":
    run()
