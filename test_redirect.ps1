$response = Invoke-WebRequest -Uri 'http://localhost:8080/shop' -MaximumRedirection 0 -ErrorAction SilentlyContinue
Write-Host "HTTP Status: $($response.StatusCode)"
Write-Host "Redirect Location: $($response.Headers.Location)"
