document.addEventListener('DOMContentLoaded', function() {
    const container = document.querySelector('.container');
    const LoginLink = document.querySelector('.SignInLink');
    const RegisterLink = document.querySelector('.SignUpLink');

    if (RegisterLink && LoginLink && container) {
        RegisterLink.addEventListener('click', function(e) {
            e.preventDefault();
            container.classList.add('active');
        });

        LoginLink.addEventListener('click', function(e) {
            e.preventDefault();
            container.classList.remove('active');
        });
    }

    const typePersonSelect = document.getElementById('type_person');
    if (typePersonSelect) {
        const specialiteContainer = document.getElementById('specialite_container');
        typePersonSelect.addEventListener('change', function() {
            if (this.value === 'medecin_specialiste') {
                if (specialiteContainer) specialiteContainer.style.display = 'block';
                const specialite = document.getElementById('specialite');
                if (specialite) specialite.required = true;
            } else {
                if (specialiteContainer) specialiteContainer.style.display = 'none';
                const specialite = document.getElementById('specialite');
                if (specialite) specialite.required = false;
            }
        });

        typePersonSelect.dispatchEvent(new Event('change'));
    }
});
