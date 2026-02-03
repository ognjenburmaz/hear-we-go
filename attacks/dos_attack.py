import requests
from concurrent.futures import ThreadPoolExecutor, as_completed

URL = "https://localhost:8080/api/content/songs"
TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGUiOiJBRE1JTiIsImNyZWF0ZWQiOjE3NzAwODQwODE4MzcsImV4cCI6MTc3MDA4NzY4MX0.0n5TG3WunAZQl3L8D7n7M2mgc3CIXXKpLOeIOQYx8Wk"

HEADERS = {
    "Authorization": f"Bearer {TOKEN}"
}

def send_request(i):
    try:
        response = requests.get(
            URL,
            headers=HEADERS,
            verify=False,   # same as curl -k
            timeout=1
        )
        return f"Request {i}: {response.status_code}"
    except Exception as e:
        return f"Request {i}: ERROR ({e})"


print("Launching 2000 parallel requests...")

with ThreadPoolExecutor(max_workers=2001) as executor:
    futures = [executor.submit(send_request, i) for i in range(1, 2001)]

    for future in as_completed(futures):
        print(future.result())

print("Attack finished.")
