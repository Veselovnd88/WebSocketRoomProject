let jwt = eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwidXNlcm5hbWUiOiJ1c2VyMSIsInJvbGUiOiJhZG1pbiJ9.vDluIRzAjSOxbq8I4tLPUR_koUl7GPkAq34xjsuA1Ds;
fetch("/api/auth/", {
        headers: {
            'Authorization': 'Bearer ' + jwt
        }
    }
).then(r =>
    r.json())
    .then(data => {
            console.log(data)
            owner = data.owner;
            username = data.username;
            console.log(owner)
        }
    )