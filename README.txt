Esta carpeta contiene el código fuente de una app Android. No es todavía un APK instalable.

FUNCIONAMIENTO
- Al activar, programa una alarma para el siguiente minuto :58 y luego cada hora.
- Reproduce el tono de alarma del teléfono hasta tocar “Detener sonido” en la notificación.
- Restaura la programación al reiniciar y al cambiar la hora/zona horaria.
- Requiere permitir notificaciones y alarmas exactas en Android.

GENERAR E INSTALAR EL APK CON ANDROID STUDIO
1. En una computadora, instala Android Studio desde https://developer.android.com/studio
2. Descomprime este ZIP y abre la carpeta AlarmaTrading en Android Studio.
3. Espera a que termine la sincronización; acepta instalar el SDK 35 si Android Studio lo solicita.
4. Ve a Build > Build Bundle(s) / APK(s) > Build APK(s).
5. Al terminar, Android Studio ofrece “locate”; el archivo queda en app/build/outputs/apk/debug/app-debug.apk.
6. Copia ese APK al teléfono, ábrelo y autoriza “Instalar apps desconocidas” para la app desde la que lo abriste (por ejemplo, Archivos).
7. Abre “AlarmaTrading”, permite notificaciones y toca “Activar alarma”. Cuando Android pida permiso para alarmas exactas, concédelo y vuelve a activar.

COMPILAR EN LA NUBE
El proyecto incluye .github/workflows/build-apk.yml. Si subes el contenido de esta carpeta a un repositorio de GitHub, el flujo compila el APK y lo guarda como artefacto en Actions. Requiere cuenta GitHub e Internet; el APK no está dentro de este ZIP.

LIMITACIONES
El sonido puede depender del volumen de alarmas, No molestar y las restricciones del fabricante. La alarma usa la hora local del dispositivo.
