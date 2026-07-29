# Subir AppGym a GitHub y actualizarla por internet

Al terminar esto, cada vez que quieras publicar una versión nueva:

1. Subes los cambios y creas una etiqueta.
2. GitHub compila la app, pasa los tests y publica el APK.
3. El móvil te avisa de que hay actualización y la instala encima. **Tus
   entrenamientos se conservan.**

Se hace una sola vez. Calcula 20 minutos.

---

## Antes de empezar: lo de la firma

Android solo deja actualizar una app si la versión nueva está firmada con **la
misma clave** que la instalada. Si cambias de clave, la única forma de instalar
es desinstalar primero, y eso **borra tus entrenamientos**.

La app usa una clave propia guardada en `%USERPROFILE%\appgym-release.jks`, con
alias `appgym` y una contraseña que generaste al crearla.

**Haz una copia de ese archivo y de la contraseña en un sitio seguro.** Sin ellos
no puedes publicar actualizaciones que se instalen encima de la app: tendrías que
desinstalar, y eso borra tus entrenamientos.

---

## Paso 1 · Crear el repositorio

1. Entra en [github.com/new](https://github.com/new).
2. Nombre: `appgym`.
3. Marca **Private**.
4. **No** marques nada de "Add a README" ni ".gitignore" ni "license".
5. Create repository.

Deja la página abierta: te va a dar unos comandos que necesitas ahora.

---

## Paso 2 · Subir el código

Abre la terminal de Android Studio (abajo del todo, pestaña **Terminal**).

Primero las cuatro primeras líneas, que no llevan nada que cambiar:

```powershell
git init
git add .
git commit -m "AppGym funcionando"
git branch -M main
```

Y ahora la dirección del repositorio. **Escribe tu usuario de GitHub donde pone
`Sallen54`** — no pegues la línea sin mirar:

```powershell
git remote add origin https://github.com/Sallen54/appgym.git
git remote -v
git push -u origin main
```

`git remote -v` debe enseñarte dos líneas con tu usuario. Si ves otra cosa,
mira más abajo en "Si algo va mal".

Te pedirá usuario y contraseña. **La contraseña no vale**: necesitas un token.

### Crear el token

1. [github.com/settings/tokens](https://github.com/settings/tokens) → *Generate
   new token* → **classic**.
2. Note: `appgym`.
3. Expiration: `No expiration` (o un año, como prefieras).
4. Marca la casilla **repo** (la de arriba, que marca todas las de dentro).
5. Generate token y **cópialo**. Solo se ve una vez.

Ese token es la "contraseña" cuando git te la pida. Guárdalo, lo vas a
necesitar otra vez para Obtainium.

### Si algo va mal

**`error: remote origin already exists`** — la dirección ya estaba guardada, y
`git remote add` no la reemplaza. Corrígela con:

```powershell
git remote set-url origin https://github.com/Sallen54/appgym.git
```

**`src refspec main does not match any`** — falta el commit. Haz `git add .` y
`git commit -m "AppGym funcionando"` antes de volver a empujar.

**`Authentication failed`** — la contraseña de GitHub no vale aquí. Como
contraseña hay que poner el token. Si te sigue rechazando, es que el token no
tiene permiso de escritura sobre el repositorio.

**`Repository not found`** con un repositorio privado — casi siempre es el token:
o no tiene permiso, o has escrito mal el nombre del repositorio.

**`ERROR_PATH_NOT_FOUND` al codificar la clave** — estás usando sintaxis de CMD
en PowerShell. En PowerShell las variables son `$env:USERPROFILE`, no
`%USERPROFILE%`.

**`Password is not ASCII` al crear la clave** — la contraseña lleva una eñe, una
tilde o algún carácter raro. Solo letras y números sin acentos.

**`base64: invalid input` en GitHub** — el secreto se pegó incompleto. Compara el
número de "Caracteres recibidos" del log con el que te dio `$b64.Length`.

---

## Paso 3 · Subir tu clave de firma como secreto

GitHub necesita tu clave para firmar el APK. Se sube convertida a texto.

Si aún no tienes la clave, este bloque la crea, genera una contraseña y deja el
secreto copiado. La contraseña debe ser **ASCII**: sin eñes, sin tildes.

```powershell
$pass = -join ((48..57) + (65..90) + (97..122) | Get-Random -Count 24 | % {[char]$_})
$kt = "C:\Program Files\Android\Android Studio\jbr\bin\keytool.exe"
& $kt -genkeypair -v -keystore "$env:USERPROFILE\appgym-release.jks" -alias appgym -keyalg RSA -keysize 2048 -validity 10000 -storepass $pass -keypass $pass -dname "CN=Alejandro, O=AppGym, C=ES"
Write-Host "GUARDA ESTA CONTRASENA:" -ForegroundColor Yellow
Write-Host $pass
```

Y para copiar el secreto cuando lo necesites:

```powershell
$b64 = [Convert]::ToBase64String([IO.File]::ReadAllBytes("$env:USERPROFILE\appgym-release.jks"))
$b64 | Set-Clipboard
$b64.Length
```

**Apunta ese número.** El workflow imprime cuántos caracteres recibe, así que si
no coinciden sabrás al instante que el pegado se cortó.

Ahora en GitHub, dentro de tu repositorio:

**Settings → Secrets and variables → Actions → New repository secret**

Crea estos cuatro:

| Name | Secret |
|---|---|
| `KEYSTORE_BASE64` | el texto largo que acabas de copiar |
| `KEYSTORE_PASSWORD` | tu contraseña |
| `KEY_ALIAS` | `appgym` |
| `KEY_PASSWORD` | la misma contraseña |

---

## Paso 4 · Publicar la primera versión

```powershell
git tag v1.0
git push origin v1.0
```

Ve a la pestaña **Actions** de tu repositorio. Verás la compilación en marcha.
Tarda unos 5 minutos. Cuando acabe, en **Releases** tendrás `appgym-1.0.apk`.

Si algo falla, entra en la ejecución y mira qué paso está en rojo: el mensaje
suele decir exactamente qué falta.

---

## Paso 5 · Configurar el móvil con Obtainium

1. Instala **Obtainium** desde
   [su página de releases](https://github.com/ImranR98/Obtainium/releases) — baja
   el archivo que acaba en `.apk`.
2. Ábrelo y ve a **Settings → Source-specific → GitHub → Personal Access Token**.
   Pega el token del paso 2. *(Esto hace falta porque tu repositorio es privado;
   sin el token Obtainium no lo ve.)*
3. Vuelve a **Add App** y pega la dirección de tu repositorio:
   `https://github.com/Sallen54/appgym`
4. **Add**.

Ya está. Obtainium comprueba solo si hay versión nueva y te avisa. Al tocar la
notificación te instala la actualización encima, conservando tus datos.

---

## El día a día

Cuando quieras publicar una versión nueva:

```powershell
git add .
git commit -m "lo que has cambiado"
git push
git tag v1.1
git push origin v1.1
```

Cinco minutos después, el móvil te avisa.

**Sube el número de la etiqueta cada vez** (v1.1, v1.2, v1.3…). Si repites una
etiqueta, GitHub no publica nada nuevo.

---

## Cosas que conviene saber

**Cada push a `main` pasa los tests automáticamente.** Si rompes algo, te sale un
aspa roja en GitHub antes de que llegue al móvil. La publicación del APK solo
ocurre con las etiquetas, así que puedes subir código a medias sin miedo.

**Tu clave de firma no está en el repositorio.** El `.gitignore` excluye
`*.jks`, `*.keystore` y `keystore.properties`. Está solo como secreto en GitHub,
que es donde debe estar.

**El repositorio es tu copia de seguridad del código, no de tus entrenamientos.**
Los datos viven solo en el móvil. Para conservarlos, exporta el informe de vez en
cuando desde Progreso.

**Si algún día pierdes el ordenador**, con el repositorio recuperas todo el
código. Pero la clave de firma está en `%USERPROFILE%\appgym-release.jks` y esa
no se sube a ningún sitio: cópiala a un lugar seguro junto con su contraseña. Sin
ellas no podrás publicar actualizaciones que se instalen encima de la app.

**El proyecto no trae `gradlew`.** No pasa nada: Android Studio usa su propio
Gradle, y GitHub instala el suyo. Si algún día quieres usar `./gradlew` desde la
terminal, ejecuta una vez `gradle wrapper --gradle-version 8.9`.
