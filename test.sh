#/bin/sh
set -euo pipefail

# Register a new user
username="$(uuidgen)"
email="${username}@example.com"
password='Password123!'
echo "Register a new user"
curl --silent -X POST http://localhost:8080/api/register -H "Content-Type: application/json" -d '{"email":"'"${email}"'","password":"'"${password}"'"}' | jq .

# Login
echo "Login"
token="$(curl --silent -X POST http://localhost:8080/api/login -H "Content-Type: application/json" -d '{"email":"'"${email}"'","password":"'"${password}"'"}'  | jq -r .token)"
echo "Token: ${token}"

# Retrive profile
echo "Retrieve profile"
curl --silent -X GET http://localhost:8080/api/me -H "Authorization: Bearer ${token}"  | jq .

# Create a new task
echo "Create a new task"
task_id="$(curl --silent -X POST http://localhost:8081/api/tasks -H "Content-Type: application/json" -H "Authorization: Bearer ${token}" -d '{"title":"New User Task","description":"This is a task for the new user"}'  | jq -r .id)"
echo "Task ID: ${task_id}"

# List all tasks
echo "List all tasks"
curl --silent -X GET http://localhost:8081/api/tasks -H "Authorization: Bearer ${token}"  | jq .

# Update a task
echo "Update a task"
task_id="$(curl --silent -X PUT "http://localhost:8081/api/tasks/${task_id}" -H "Content-Type: application/json" -H "Authorization: Bearer ${token}" -d '{"title":"Updated Task Title","description":"This is an updated task description","completed":true}'  | jq -r .id)"
echo "Task ID: ${task_id}"

# List all tasks
echo "List all tasks"
curl --silent -X GET http://localhost:8081/api/tasks -H "Authorization: Bearer ${token}"  | jq .

# Delete a task
echo "Delete a task"
curl --silent -X DELETE "http://localhost:8081/api/tasks/${task_id}" -H "Authorization: Bearer ${token}" | jq .

# List all tasks
echo "List all tasks"
curl --silent -X GET http://localhost:8081/api/tasks -H "Authorization: Bearer ${token}"  | jq .

# Create another task
echo "Create another task"
task_id="$(curl --silent -X POST http://localhost:8081/api/tasks -H "Content-Type: application/json" -H "Authorization: Bearer ${token}" -d '{"title":"Another Task","description":"This is another task after deleting the previous one"}'  | jq .id)"

# List all tasks
echo "List all tasks"
curl --silent -X GET http://localhost:8081/api/tasks -H "Authorization: Bearer ${token}"  | jq .

echo "Deleting all created tasks..."
task_ids="$(curl --silent -X GET http://localhost:8081/api/tasks -H "Authorization: Bearer ${token}"  | jq -r '.[] | .id')"
while IFS= read -r id; do
  [ -z "$id" ] && continue
  echo "Processing task $id"
  curl --silent -X DELETE "http://localhost:8081/api/tasks/${id}" -H "Authorization: Bearer ${token}" | jq .
done <<< "$task_ids"