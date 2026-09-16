#Requires -Version 5.1
$ErrorActionPreference = 'Stop'

# Windows PowerShell 5.1 can still default to TLS versions that GitHub, Modrinth and CurseForge refuse.
[Net.ServicePointManager]::SecurityProtocol = [Net.ServicePointManager]::SecurityProtocol -bor [Net.SecurityProtocolType]::Tls12

$repository = 'Bram1903/MarlowsCrystalOptimizer'
# GitHub fills in these token fields from the URL. It cannot preselect the repository.
$githubTokenUrl = 'https://github.com/settings/personal-access-tokens/new?name=MarlowsCrystalOptimizer%20publishing&description=Creates%20releases%20for%20Bram1903%2FMarlowsCrystalOptimizer&expires_in=365&contents=write'

function Get-Status([string]$Uri, [hashtable]$Headers = @{}) {
    try {
        (Invoke-WebRequest -Uri $Uri -Headers $Headers -UserAgent "$repository publishing setup" -UseBasicParsing -TimeoutSec 15).StatusCode
    } catch {
        $response = $_.Exception.Response
        if ($response) { [int]$response.StatusCode } else { 0 }
    }
}

function Test-DiscordWebhook([string]$Url) {
    if ($Url -notmatch '^https://(discord|discordapp)\.com/api/webhooks/') { return 'not a Discord webhook URL' }
    Get-Status $Url
}

function Confirm-Choice([string]$Prompt, [bool]$Default) {
    $answer = Read-Host -Prompt "  $Prompt"
    if (-not $answer) { return $Default }
    return $answer -match '^(y|yes)$'
}

function Read-Hidden([string]$Prompt) {
    $secure = Read-Host -Prompt $Prompt -AsSecureString
    $bstr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secure)
    try { [Runtime.InteropServices.Marshal]::PtrToStringBSTR($bstr) } finally { [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($bstr) }
}

$secrets = @(
    [pscustomobject]@{
        Name = 'GITHUB_TOKEN'; Required = $true
        For = 'Creating the GitHub release and attaching every jar to it'
        Url = $githubTokenUrl
        Steps = 'Everything on the page is filled in except Repository access: choose Only select repositories, pick MarlowsCrystalOptimizer, then Generate token.'
        Check = { param($value) Get-Status "https://api.github.com/repos/$repository" @{ Authorization = "Bearer $value" } }
    }
    [pscustomobject]@{
        Name = 'MODRINTH_TOKEN'; Required = $true
        For = 'Uploading one Modrinth version per supported range'
        Url = 'https://modrinth.com/settings/pats'
        Steps = 'Create a PAT with the Create versions, Read versions and Write versions scopes.'
        Check = { param($value) Get-Status 'https://api.modrinth.com/v2/user' @{ Authorization = $value } }
    }
    [pscustomobject]@{
        Name = 'CURSEFORGE_TOKEN'; Required = $true
        For = 'Uploading one CurseForge file per supported range'
        Url = 'https://authors-old.curseforge.com/account/api-tokens'
        Steps = 'Generate a token with any name. CurseForge tokens have no permissions to choose.'
        Check = { param($value) Get-Status 'https://minecraft.curseforge.com/api/game/version-types' @{ 'X-Api-Token' = $value } }
    }
    [pscustomobject]@{
        Name = 'DISCORD_WEBHOOK'; Required = $true
        For = 'Announcing the release in Discord'
        Url = ''
        Steps = 'In Discord: Edit Channel > Integrations > Webhooks > New Webhook > Copy Webhook URL.'
        Check = { param($value) Test-DiscordWebhook $value }
    }
    [pscustomobject]@{
        Name = 'DISCORD_WEBHOOK_DRY_RUN'; Required = $false
        For = 'Previewing the announcement from a dry run, in a test channel'
        Url = ''
        Steps = 'In Discord, for a test channel: Edit Channel > Integrations > Webhooks > New Webhook > Copy Webhook URL.'
        Check = { param($value) Test-DiscordWebhook $value }
    }
)

Write-Host "Marlow's Crystal Optimizer publishing setup"
Write-Host ''
Write-Host 'This helps you create every secret that .\gradlew publishMods needs, checks each one against its service, and'
Write-Host 'stores them as environment variables for your Windows account. Pasted values are hidden.'

$values = @{}
foreach ($secret in $secrets) {
    $current = [Environment]::GetEnvironmentVariable($secret.Name, 'User')
    $values[$secret.Name] = $current
    $label = if ($secret.Required) { '' } else { ' (optional)' }

    Write-Host ''
    Write-Host "$($secret.Name)$label"
    Write-Host "  Used for: $($secret.For)"

    if ($current -and -not (Confirm-Choice 'Already set. Replace it? [y/N]' $false)) { continue }
    Write-Host "  $($secret.Steps)"
    if ($secret.Url -and (Confirm-Choice 'Open the page to create one in your browser? [Y/n]' $true)) {
        Start-Process $secret.Url
        Write-Host "  If no browser opened, go to $($secret.Url)"
    }

    while ($true) {
        $prompt = if ($current) { '  Paste it, or press Enter to keep the current one' } else { '  Paste it, or press Enter to skip' }
        $value = Read-Hidden $prompt
        if (-not $value) { break }

        $result = & $secret.Check $value
        if ($result -eq 200) { $current = $value; Write-Host '  Accepted.'; break }
        if ($result -eq 0) { $current = $value; Write-Host '  Could not reach the service to check it, keeping it anyway.'; break }
        Write-Host "  Rejected ($result)."
    }

    $values[$secret.Name] = $current
}

foreach ($secret in $secrets) {
    if ($values[$secret.Name]) {
        [Environment]::SetEnvironmentVariable($secret.Name, $values[$secret.Name], 'User')
    }
}

$missing = @($secrets | Where-Object { $_.Required -and -not $values[$_.Name] } | ForEach-Object { $_.Name })

Write-Host ''
Write-Host 'Saved as user environment variables.'
Write-Host 'Open a new terminal, and restart your IDE, before publishing.'
if ($missing.Count -gt 0) {
    Write-Host "Still missing: $($missing -join ', '). publishMods will not upload anything until they are set."
}
