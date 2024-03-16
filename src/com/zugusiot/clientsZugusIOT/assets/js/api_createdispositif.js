function createDispositif() {
    const form = document.getElementById('form-create-dispositif');
    const formData = new FormData(form);
    
    const newDispositif = {};
    formData.forEach((value, key) => {
        newDispositif[key] = value;
    });

    fetch('http://192.168.2.29:8080/api/sensors', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(newDispositif),
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then(data => {
        console.log('Success:', data);
        // Ici, gérer la réponse de succès
    })
    .catch((error) => {
        console.error('Error:', error);
    });
}


