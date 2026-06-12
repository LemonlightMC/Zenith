# Remove root-level bin and build folders
$rootBinPath = Join-Path (Get-Location) "bin"
$rootBuildPath = Join-Path (Get-Location) "build"

if (Test-Path $rootBinPath) {
    Remove-Item -Path $rootBinPath -Recurse -Force
    Write-Host "Removed: $rootBinPath"
}

if (Test-Path $rootBuildPath) {
    Remove-Item -Path $rootBuildPath -Recurse -Force
    Write-Host "Removed: $rootBuildPath"
}

# Remove bin and build folders from zenith-* directories
Get-ChildItem -Directory -Filter "packages/" | ForEach-Object {
    $binPath = Join-Path $_.FullName "bin"
    $buildPath = Join-Path $_.FullName "build"
    
    if (Test-Path $binPath) {
        Remove-Item -Path $binPath -Recurse -Force
        Write-Host "Removed: $binPath"
    }
    
    if (Test-Path $buildPath) {
        Remove-Item -Path $buildPath -Recurse -Force
        Write-Host "Removed: $buildPath"
    }
}