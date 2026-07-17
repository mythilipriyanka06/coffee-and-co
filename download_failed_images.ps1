$targetDir = Join-Path $PSScriptRoot 'src\main\resources\static\images\products'
if (-not (Test-Path $targetDir)) {
    New-Item -ItemType Directory -Path $targetDir -Force | Out-Null
}

$images = @{}

$images['double_espresso.jpg'] = 'https://images.unsplash.com/photo-1497935586351-b67a49e012bf?w=600&h=600&fit=crop'
$images['south_indian_coffee.jpg'] = 'https://images.unsplash.com/photo-1507133750040-4a8f57021571?w=600&h=600&fit=crop'
$images['cold_mocha.jpg'] = 'https://images.unsplash.com/photo-1557142046-c704a3adf364?w=600&h=600&fit=crop'
$images['vanilla_cold_coffee.jpg'] = 'https://images.unsplash.com/photo-1517701604599-bb29b565090c?w=600&h=600&fit=crop'
$images['hibiscus_tea.jpg'] = 'https://images.unsplash.com/photo-1506084868230-bb9d95c24759?w=600&h=600&fit=crop'
$images['chocolate_ice_cream.jpg'] = 'https://images.unsplash.com/photo-1567206563064-6f60f40a2b57?w=600&h=600&fit=crop'
$images['vanilla_ice_cream.jpg'] = 'https://images.unsplash.com/photo-1576506295286-5cda18df43e7?w=600&h=600&fit=crop'
$images['cheesecake.jpg'] = 'https://images.unsplash.com/photo-1606890737304-57a1ca8a5b62?w=600&h=600&fit=crop'
$images['blueberry_muffin.jpg'] = 'https://images.unsplash.com/photo-1607958996333-41aef7caefaa?w=600&h=600&fit=crop'
$images['cupcake.jpg'] = 'https://images.unsplash.com/photo-1576618148400-f54bed99fcfd?w=600&h=600&fit=crop'
$images['croissant.jpg'] = 'https://images.unsplash.com/photo-1608198093002-ad4e005484ec?w=600&h=600&fit=crop'
$images['banana_bread.jpg'] = 'https://images.unsplash.com/photo-1534422298391-e4f8c172dddb?w=600&h=600&fit=crop'
$images['veg_puff.jpg'] = 'https://images.unsplash.com/photo-1619860860774-1e2e17343432?w=600&h=600&fit=crop'
$images['paneer_puff.jpg'] = 'https://images.unsplash.com/photo-1544025162-d76694265947?w=600&h=600&fit=crop'
$images['spring_roll.jpg'] = 'https://images.unsplash.com/photo-1563245372-f21724e3856d?w=600&h=600&fit=crop'
$images['cheese_garlic_bread.jpg'] = 'https://images.unsplash.com/photo-1541532713592-79a0317b6b77?w=600&h=600&fit=crop'
$images['mexican_sandwich.jpg'] = 'https://images.unsplash.com/photo-1539252554453-80ab65ce3586?w=600&h=600&fit=crop'
$images['corn_burger.jpg'] = 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=600&h=600&fit=crop'
$images['mushroom_pasta.jpg'] = 'https://images.unsplash.com/photo-1551183053-bf91a1d81141?w=600&h=600&fit=crop'
$images['popcorn.jpg'] = 'https://images.unsplash.com/photo-1578844251758-2f71da64c96f?w=600&h=600&fit=crop'
$images['chocolate_donut.jpg'] = 'https://images.unsplash.com/photo-1612240498936-65f5101365d2?w=600&h=600&fit=crop'

$total = $images.Count
$current = 0
$success = 0
$failed = 0

Write-Host ''
Write-Host '========================================'
Write-Host '  Coffee and Co - Targeted Downloader'
Write-Host "  Downloading $total missing product images..."
Write-Host '========================================'
Write-Host ''

foreach ($entry in $images.GetEnumerator()) {
    $current++
    $filename = $entry.Key
    $url = $entry.Value
    $filepath = Join-Path $targetDir $filename

    Write-Host "[$current/$total] $filename ... " -NoNewline

    try {
        $wc = New-Object System.Net.WebClient
        $wc.Headers.Add('User-Agent', 'Mozilla/5.0')
        $wc.DownloadFile($url, $filepath)

        $fileSize = (Get-Item $filepath).Length
        if ($fileSize -gt 1000) {
            $kb = [math]::Round($fileSize / 1024)
            Write-Host "OK ${kb}KB" -ForegroundColor Green
            $success++
        } else {
            Write-Host 'TOO SMALL' -ForegroundColor Yellow
            $failed++
        }
    } catch {
        $emsg = $_.Exception.Message
        Write-Host "FAILED" -ForegroundColor Red
        Write-Host "  -> $emsg" -ForegroundColor DarkRed
        $failed++
    }
}

Write-Host ''
Write-Host '========================================'
Write-Host "  Done! Success=$success Failed=$failed Total=$total"
Write-Host '========================================'
