function fetchAndDisplayuserdispositifs() {
    fetch('http://127.0.0.1:8000/accounts/api/donnees-user-dispositifs/') // Remplacez par l'URL de votre API
        .then(response => response.json())
        .then(userdispositifs => {
            const tableBody = document.getElementById('tableuserdispositifs').getElementsByTagName('tbody')[0];
            tableBody.innerHTML = ''; // Effacer les anciennes lignes

            userdispositifs.forEach(userdispositif => {
                const row = tableBody.insertRow();
                row.insertCell(0).innerHTML = userdispositif.id;
                row.insertCell(1).innerHTML = userdispositif.code;
                row.insertCell(2).innerHTML = userdispositif.dispositif;
                row.insertCell(3).innerHTML = userdispositif.user;
                row.insertCell(4).innerHTML = userdispositif.localisation;
                
            });
        })
        .catch(error => console.error('Error:', error));
}

document.addEventListener('DOMContentLoaded', () => {
    fetchAndDisplayuserdispositifs();
});

// Rafraichir les données toutes les 3 secondes
setInterval(fetchAndDisplayuserdispositifs, 3000);
