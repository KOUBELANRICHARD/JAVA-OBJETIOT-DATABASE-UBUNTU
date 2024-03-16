function fetchAndDisplaydispositifs() {
    fetch('http://192.168.2.29:8080/api/sensors') // Remplacez par l'URL de votre API
        .then(response => response.json())
        .then(dispositifs => {
            const tableBody = document.getElementById('tabledispositifs').getElementsByTagName('tbody')[0];
            tableBody.innerHTML = ''; // Effacer les anciennes lignes

            dispositifs.forEach(dispositif => {
                const row = tableBody.insertRow();
                row.insertCell(0).innerHTML = dispositif.id;
                row.insertCell(1).innerHTML = dispositif.code;
                row.insertCell(2).innerHTML = dispositif.nom;
                row.insertCell(3).innerHTML = dispositif.type;
				row.insertCell(4).innerHTML = dispositif.matrice;
                row.insertCell(5).innerHTML = dispositif.description;
				row.insertCell(6).innerHTML = dispositif.etat;
				row.insertCell(7).innerHTML = dispositif.position;
				row.insertCell(8).innerHTML = dispositif.creation_date;
				
                
            });
        })
		.then(dispositifs => {
    console.log(dispositifs); // Vérifiez la structure de données reçue.
    const tableBody = document.getElementById('tabledispositifs').getElementsByTagName('tbody')[0];
    console.log(tableBody); // Assurez-vous que tableBody n'est pas null.
    // ... le reste de votre code ...
})

        .catch(error => console.error('Error:', error));
}

document.addEventListener('DOMContentLoaded', () => {
    fetchAndDisplaydispositifs();
});

// Rafraichir les données toutes les 3 secondes
setInterval(fetchAndDisplaydispositifs, 3000);


