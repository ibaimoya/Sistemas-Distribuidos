/**
 * Alterna la visibilidad de la contraseña en un campo de entrada y actualiza los elementos de la interfaz
 * @param {Event} event - El objeto evento DOM del elemento que disparó la acción
 * @description Esta función alterna el tipo de entrada entre "password" y "text",
 * y actualiza el icono y texto asociados para reflejar el estado actual de visibilidad.
 * Cuando la contraseña es visible, muestra un icono de "ocultar" (🙈) y el texto "Ocultar".
 * Cuando está oculta, muestra un icono de "ojo" (👁) y el texto "Mostrar".
 * 
 * @author Ibai Moya Aroz
 * 
 * @version 1.0.0
 * @since 1.0.0
 */
function mostrarContraseña(event) {
    var input = document.getElementById("password")
    var icono = event.currentTarget.querySelector("#icono")
    var texto = event.currentTarget.querySelector("#texto")
  
    if (input.type === "password") {
      input.type = "text"
      icono.textContent = "🙈"
      texto.textContent = "Ocultar"
    } else {
      input.type = "password"
      icono.textContent = "👁"
      texto.textContent = "Mostrar"
    }
}