param(
    [string]$dotFilePath
)

function Test-GraphViz {
    try {
        $null = Get-Command dot -ErrorAction Stop
        return $true
    }
    catch {
        return $false
    }
}

function Show-GraphVizInstallHelp {
    Write-Host "`n=== ATTENZIONE: Graphviz non trovato ===" -ForegroundColor Red
    Write-Host "Per visualizzare i grafi, installa Graphviz:" -ForegroundColor Yellow
    Write-Host "`Riavvia PowerShell dopo l'installazione" -ForegroundColor Yellow
    Write-Host "=========================================`n" -ForegroundColor Red
}

Write-Host "=== Graph Visualization ===" -ForegroundColor Green
Write-Host "Input DOT file: $dotFilePath" -ForegroundColor Gray

if (-not (Test-Path $dotFilePath)) {
    Write-Host "ERRORE: File DOT non trovato: $dotFilePath" -ForegroundColor Red
    exit 1
}

if (-not (Test-GraphViz)) {
    Show-GraphVizInstallHelp
    exit 1
}

$dotFile = Get-Item $dotFilePath
$outputDir = $dotFile.DirectoryName
$baseName = [System.IO.Path]::GetFileNameWithoutExtension($dotFile.Name)
$outputPath = [System.IO.Path]::Combine($outputDir, "$baseName.png")

Write-Host "Converting DOT to PNG..." -ForegroundColor Yellow
Write-Host "Output: $outputPath" -ForegroundColor Gray

try {
    $process = Start-Process -FilePath "dot" -ArgumentList "-Tpng `"$dotFilePath`" -o `"$outputPath`"" -Wait -PassThru -NoNewWindow
    
    if ($process.ExitCode -eq 0 -and (Test-Path $outputPath)) {
        Write-Host "SUCCESS: Graph image generated!" -ForegroundColor Green
        #Write-Host "File size: $((Get-Item $outputPath).Length) bytes" -ForegroundColor Gray
        
        # ✅ MOSTRA AUTOMATICAMENTE l'immagine senza chiedere
        Write-Host "Apertura automatica dell'immagine..." -ForegroundColor Green
        Invoke-Item $outputPath
    }
    else {
        Write-Host "ERRORE: Conversione fallita (Exit code: $($process.ExitCode))" -ForegroundColor Red
        Write-Host "Controlla che il file DOT sia valido" -ForegroundColor Yellow
    }
}
catch {
    Write-Host "ERRORE durante la conversione: $($_.Exception.Message)" -ForegroundColor Red
}