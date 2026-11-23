# (Sin tildes a proposito: PowerShell 5 lee mal los acentos de este archivo.)
# Sube los cambios y publica una version nueva del APK.
#
# Uso, desde la terminal de Android Studio:
#   powershell -ExecutionPolicy Bypass -File .\publicar.ps1 1.4 "Lo que has cambiado"
#
# GitHub compila y publica el APK en unos 5 minutos. Mira la pestana Actions.

param(
    [Parameter(Mandatory = $true)][string]$Version,
    [string]$Mensaje = "Version $Version"
)

$tag = "v$Version"

if (git tag --list $tag) {
    Write-Host "La etiqueta $tag ya existe. Usa un numero nuevo (por ejemplo, sube el ultimo digito)." -ForegroundColor Red
    exit 1
}

# Documentos personales que no deben ir al repositorio publico.
if (git ls-files "Plan-*.pdf") { git rm --cached --quiet Plan-*.pdf }

git add .
git commit -m $Mensaje
if ($LASTEXITCODE -ne 0) { Write-Host "No habia nada nuevo que guardar." -ForegroundColor Yellow }

git push
if ($LASTEXITCODE -ne 0) { Write-Host "Fallo el push. Revisa el mensaje de arriba." -ForegroundColor Red; exit 1 }

git tag $tag
git push origin $tag
if ($LASTEXITCODE -ne 0) { Write-Host "Fallo al subir la etiqueta." -ForegroundColor Red; exit 1 }

Write-Host ""
Write-Host "Listo. En 5 minutos tendras appgym-$Version.apk en:" -ForegroundColor Green
Write-Host "https://github.com/Sallen54/appgym/releases"
