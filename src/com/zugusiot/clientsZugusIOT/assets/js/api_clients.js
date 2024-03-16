function fetchAndDisplayClients() {
    fetch('http://127.0.0.1:8000/accounts/api/donnees-clients/') // Remplacez par l'URL de votre API
        .then(response => response.json())
        .then(clients => {
            const tableBody = document.getElementById('tableclients').getElementsByTagName('tbody')[0];
            tableBody.innerHTML = ''; // Effacer les anciennes lignes

            clients.forEach(client => {
                const row = tableBody.insertRow();
                row.insertCell(0).innerHTML = client.id;
                row.insertCell(1).innerHTML = client.first_name;
                row.insertCell(2).innerHTML = client.last_name;
                row.insertCell(3).innerHTML = client.email;
                row.insertCell(4).innerHTML = client.adresse;
                row.insertCell(5).innerHTML = client.username;
            });
        })
        .catch(error => console.error('Error:', error));
}

document.addEventListener('DOMContentLoaded', () => {
    fetchAndDisplayClients();
});

// Rafraichir les données toutes les 3 secondes
setInterval(fetchAndDisplayClients, 3000);
