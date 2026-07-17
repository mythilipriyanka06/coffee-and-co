$css = Get-Content 'c:\Users\Mythili S\Downloads\coffee and co\coffee-and-co\src\main\resources\static\css\premium.css'
$targets = @('spin-fab', 'surprise-fab', 'loyalty-widget', 'music-fab', 'music-tooltip')
for ($i = 0; $i -lt $css.Length; $i++) {
    foreach ($t in $targets) {
        if ($css[$i] -like "*$t*") {
            Write-Host "Line $($i+1)`: $($css[$i].Trim())"
        }
    }
}
