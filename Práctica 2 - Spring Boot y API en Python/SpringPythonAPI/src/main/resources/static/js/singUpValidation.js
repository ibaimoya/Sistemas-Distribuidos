/**
 * Validación del formulario de inicio de sesión
 * @description Esta función valida el formulario de registro al enviarlo.
 * Verifica que los campos de usuario, email, contraseña y confirmación no estén vacíos.
 * Si alguno de los campos está vacío, muestra un mensaje de error correspondiente.
 * Si todos los campos son válidos, el formulario se envía normalmente.
 * 
 * @author Ibai Moya Aroz
 * 
 * @version 1.0.0
 * @since 1.0.0
 */
document.getElementById('formRegistro').addEventListener('submit', ev => {
    const u = usuario, e = email, p = password, c = confirm;
    let ok = true;
    ['usuario', 'email', 'pass', 'confirm'].forEach(id =>
      document.getElementById(id + 'Error').textContent = ''
    );
  
    if (!u.value.trim()) { usuarioError.textContent = 'El nombre de usuario es obligatorio.'; ok = false; }
    if (!e.value.trim()) { emailError.textContent   = 'El correo electrónico es obligatorio.'; ok = false; }
    if (!p.value.trim()) { passError.textContent    = 'La contraseña es obligatoria.'; ok = false; }
    if (c.value !== p.value) { confirmError.textContent = 'No coincide'; ok = false; }
  
    if (!ok) ev.preventDefault();
  });