// script/includes.js
document.addEventListener('DOMContentLoaded', async () => {
    // Função genérica para carregar um componente
    async function carregarComponente(id, arquivo) {
        const el = document.getElementById(id);
        if (!el) return;
        try {
            const res = await fetch(arquivo);
            if (!res.ok) throw new Error(`Erro ao carregar ${arquivo}`);
            const html = await res.text();
            el.innerHTML = html;
        } catch (err) {
            console.error(`Falha ao carregar ${arquivo}:`, err);
        }
    }

    // Carrega navbar e footer
    await Promise.all([
        carregarComponente('navbar', '/navbar.html'),
        carregarComponente('footer', '/footer.html')
    ]);

    // Aguarda um pequeno delay para garantir que navbar/footer estejam injetados
    setTimeout(() => {
        const logoutBtn = document.querySelector('#logoutBtn');
        if (logoutBtn) {
            logoutBtn.addEventListener('click', () => {
                localStorage.removeItem('usuario');
                Toastify({
                    text: "Logout realizado com sucesso!",
                    duration: 2000,
                    gravity: "top",
                    position: "right",
                    style: { background: "linear-gradient(to right, #ff5f6d, #ffc371)" }
                }).showToast();
                setTimeout(() => window.location.href = '/login.html', 1500);
            });
        }
    }, 300);
});
