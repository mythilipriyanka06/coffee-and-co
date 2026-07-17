$dir = 'c:\Users\Mythili S\Downloads\coffee and co\coffee-and-co\src\main\resources\static\images\combos'

Invoke-WebRequest -Uri 'https://images.unsplash.com/photo-1524350876685-274059332603?w=600&q=80' -OutFile "$dir\latte.jpg" -TimeoutSec 30 -UserAgent 'Mozilla/5.0'
Write-Host 'latte done'

Invoke-WebRequest -Uri 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=600&q=80' -OutFile "$dir\scone.jpg" -TimeoutSec 30 -UserAgent 'Mozilla/5.0'
Write-Host 'scone done'

Write-Host 'Fixed.'
