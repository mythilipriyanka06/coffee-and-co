try {
  $r = Invoke-WebRequest -Uri 'http://localhost:8080/images/combos/cappuccino.jpg' -Method Head -TimeoutSec 5
  Write-Host "Status: $($r.StatusCode)"
} catch {
  Write-Host "Error: $($_.Exception.Message)"
}

try {
  $r2 = Invoke-WebRequest -Uri 'http://localhost:8080/images/logo.png' -Method Head -TimeoutSec 5
  Write-Host "Logo Status: $($r2.StatusCode)"
} catch {
  Write-Host "Logo Error: $($_.Exception.Message)"
}
