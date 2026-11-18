$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent $PSScriptRoot
Set-Location $repoRoot

$jdkCandidates = @(
    "C:\Users\T14\.vscode\extensions\redhat.java-1.58.0-win32-x64\jre\25.0.4.1-win32-x86_64",
    $env:JAVA_HOME,
    "C:\Program Files\JetBrains\IntelliJ IDEA 2024.2.3\jbr"
)

$javaHome = $jdkCandidates |
    Where-Object { $_ -and (Test-Path (Join-Path $_ "bin\java.exe")) } |
    Select-Object -First 1

if (-not $javaHome) {
    throw "JDK nao encontrado. Instale o JDK 25 ou configure JAVA_HOME."
}

$env:JAVA_HOME = $javaHome
$env:PATH = "$(Join-Path $javaHome "bin");$env:PATH"

$nativeDir = Join-Path $repoRoot "target\native\x64"
$env:JDK_JAVA_OPTIONS = "-Djava.library.path=`"$nativeDir`""

$maven = Get-Command mvn.cmd -ErrorAction SilentlyContinue
if (-not $maven) {
    $maven = Get-Command mvn -ErrorAction SilentlyContinue
}
if (-not $maven) {
    throw "Maven nao encontrado no PATH."
}

$envFile = Join-Path $repoRoot ".env"
if (Test-Path $envFile) {
    Get-Content $envFile | Where-Object { $_ -match "^[A-Za-z_][A-Za-z0-9_]*=" } |
        ForEach-Object {
            $name, $value = $_ -split "=", 2
            [Environment]::SetEnvironmentVariable($name.Trim(), $value.Trim(), "Process")
        }
}

if (-not $env:DB_URL) {
    $env:DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=Expansaodb;encrypt=true;trustServerCertificate=true;integratedSecurity=true"
}

& $maven.Source spring-boot:run
