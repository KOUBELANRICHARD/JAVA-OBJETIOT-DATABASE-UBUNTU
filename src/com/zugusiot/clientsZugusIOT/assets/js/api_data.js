function loadDataAndCreateChart() {
        fetch('http://127.0.0.1:8000/accounts/api/donnees/')
            .then(response => response.json())
            .then(data => {
                const labels = data.map(e => e.date_heure);
                const values = data.map(e => e.valeur);

                const ctx = document.getElementById('myChart').getContext('2d');
                new Chart(ctx, {
                    type: 'line', // Type de graphique
                    data: {
                        labels: labels,
                        datasets: [{
                            label: 'Valeur',
                            data: values,
                            backgroundColor: 'rgba(0, 123, 255, 0.5)',
                            borderColor: 'rgba(0, 123, 255, 1)',
                            borderWidth: 1
                        }]
                    },
                    options: {
                        scales: {
                            y: {
                                beginAtZero: true
                            }
                        }
                    }
                });
            })
            .catch(error => console.error('Error:', error));
    }

    document.addEventListener('DOMContentLoaded', () => {
        loadDataAndCreateChart();
    });
	
	

// Mettez à jour les données toutes les 3 secondes
setInterval(loadDataAndCreateChart, 3000);