/** Devuelve el <div class="error"> correspondiente al campo. */
function getErrorDiv(inputId){
  const campo = document.getElementById(inputId);
  const grupo = campo.closest('.mb-3');
  return grupo ? grupo.querySelector('.error') : null;
}

/** Limpia todos los mensajes de error. */
function limpiarErrores(){
  document.querySelectorAll('#formRegistro .error').forEach(div=>div.textContent='');
}

/** Valida el formulario y muestra mensajes. */
function validateRegister(){
  const usuario  = document.getElementById('usuario');
  const email    = document.getElementById('email');
  const password = document.getElementById('password');
  const confirm  = document.getElementById('confirm');

  let valido = true;
  limpiarErrores();

  /* Usuario. */
  if(!usuario.value.trim()){
    getErrorDiv('usuario').textContent = 'El nombre de usuario es obligatorio.';
    valido = false;
  }

  // Email
  const correo = email.value.trim();
  if(!correo){
    getErrorDiv('email').textContent = 'El correo electrónico es obligatorio.';
    valido = false;
  }else if(!/^\S+@\S+\.\S+$/.test(correo)){
    getErrorDiv('email').textContent = 'Formato de correo no válido.';
    valido = false;
  }

  // Contraseña
  if(!password.value.trim()){
    getErrorDiv('password').textContent = 'La contraseña es obligatoria.';
    valido = false;
  }

  // Confirmación
  if(confirm.value !== password.value){
    getErrorDiv('confirm').textContent = 'Las contraseñas no coinciden.';
    valido = false;
  }

  return valido;
}
