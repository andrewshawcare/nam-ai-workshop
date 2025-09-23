#/bin/sh
set -euo pipefail

# Register a new user
username="$(uuidgen)"
echo "Register a new user"
curl --silent -X POST http://localhost:8080/api/register -H "Content-Type: application/json" -d '{"email":"'"${username}"'@example.com","password":"Password123!"}' | jq .

# Login
echo "Login"
token="$(curl --silent -X POST http://localhost:8080/api/login -H "Content-Type: application/json" -d '{"email":"newuser@example.com","password":"Password123!"}'  | jq -r .token)"

# Retrive profile
echo "Retrieve profile"
curl --silent -X GET http://localhost:8080/api/me -H "Authorization: Bearer ${token}"  | jq .

# Create a new task
echo "Create a new task"
curl --silent -X POST http://localhost:8081/api/tasks -H "Content-Type: application/json" -H "Authorization: Bearer ${token}" -d '{"title":"New User Task","description":"This is a task for the new user"}'  | jq .

# List all tasks
echo "List all tasks"
curl --silent -X GET http://localhost:8081/api/tasks -H "Authorization: Bearer ${token}"  | jq .

# Update a task
echo "Update a task"
curl --silent -X PUT http://localhost:8081/api/tasks/96c903fa-9305-4d48-b868-e9e83accb9eb -H "Content-Type: application/json" -H "Authorization: Bearer ${token}" -d '{"title":"Updated Task Title","description":"This is an updated task description","completed":true}'  | jq .

# List all tasks
echo "List all tasks"
curl --silent -X GET http://localhost:8081/api/tasks -H "Authorization: Bearer ${token}"  | jq .

# Delete a task
echo "Delete a task"
curl --silent -X DELETE http://localhost:8081/api/tasks/96c903fa-9305-4d48-b868-e9e83accb9eb -H "Authorization: Bearer ${token}"  | jq .

# List all tasks
echo "List all tasks"
curl --silent -X GET http://localhost:8081/api/tasks -H "Authorization: Bearer ${token}"  | jq .

# Create another task
echo "Create another task"
curl --silent -X POST http://localhost:8081/api/tasks -H "Content-Type: application/json" -H "Authorization: Bearer ${token}" -d '{"title":"Another Task","description":"This is another task after deleting the previous one"}'  | jq .

# List all tasks
echo "List all tasks"
curl --silent -X GET http://localhost:8081/api/tasks -H "Authorization: Bearer ${token}"  | jq .

echo "Deleting all created tasks..."
task_ids="$(curl --silent -X GET http://localhost:8081/api/tasks -H "Authorization: Bearer ${token}"  | jq -r '.[] | .id')"
while IFS= read -r id; do
  [ -z "$id" ] && continue
  echo "Processing task $id"
  # example action: delete each task
  curl --silent -X DELETE "http://localhost:8081/api/tasks/${id}" -H "Authorization: Bearer ${token}" | jq .
done <<< "$task_ids"