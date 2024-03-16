function confirmDeletion() {
    const code = document.getElementById('delete-code').value;

    if (code) {
        // Utilisation de SweetAlert pour confirmer la suppression
        Swal.fire({
            title: 'Êtes-vous sûr?',
            text: `Voulez-vous vraiment supprimer le dispositif avec le code: ${code}?`,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: 'Oui, supprimer!',
            cancelButtonText: 'Non, annuler'
        }).then((result) => {
            if (result.isConfirmed) {
                // Si l'utilisateur confirme, appeler la fonction de suppression
                deleteDispositif(code);
            }
        });
    }
}

function deleteDispositif(code) {
    // Appel à l'API de suppression
    fetch(`http://192.168.2.29:8080/api/sensors/${code}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then(data => {
        Swal.fire('Supprimé!', 'Le dispositif a été supprimé.', 'success');
        // Vous pouvez ici actualiser la liste des dispositifs
    })
    .catch((error) => {
        console.error('Erreur:', error);
        Swal.fire('Erreur', 'Une erreur est survenue lors de la suppression.', 'error');
    });
}
