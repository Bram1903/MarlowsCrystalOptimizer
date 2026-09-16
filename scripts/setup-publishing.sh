#!/usr/bin/env bash
set -euo pipefail

repository="Bram1903/MarlowsCrystalOptimizer"
config_dir="${XDG_CONFIG_HOME:-$HOME/.config}/marlowcrystal"
env_file="$config_dir/publishing.env"
names="GITHUB_TOKEN MODRINTH_TOKEN CURSEFORGE_TOKEN DISCORD_WEBHOOK DISCORD_WEBHOOK_DRY_RUN"
# GitHub fills in these token fields from the URL. It cannot preselect the repository.
github_token_url="https://github.com/settings/personal-access-tokens/new?name=MarlowsCrystalOptimizer%20publishing&description=Creates%20releases%20for%20Bram1903%2FMarlowsCrystalOptimizer&expires_in=365&contents=write"

abort() {
    printf '\nAborted, nothing was saved.\n'
    exit 1
}

status_of() {
    if ! command -v curl >/dev/null 2>&1; then
        printf '000'
        return
    fi
    curl -s -o /dev/null -w '%{http_code}' --max-time 15 -A "$repository publishing setup" "$@" || true
}

check_github() {
    status_of -H "Authorization: Bearer $1" "https://api.github.com/repos/$repository"
}

check_modrinth() {
    status_of -H "Authorization: $1" "https://api.modrinth.com/v2/user"
}

check_curseforge() {
    status_of -H "X-Api-Token: $1" "https://minecraft.curseforge.com/api/game/version-types"
}

check_discord() {
    case "$1" in
        https://discord.com/api/webhooks/* | https://discordapp.com/api/webhooks/*) status_of "$1" ;;
        *) printf 'not a Discord webhook URL' ;;
    esac
}

confirm() {
    local answer
    printf '  %s ' "$1"
    IFS= read -r answer || abort
    case "$(printf '%s' "${answer:-$2}" | tr '[:upper:]' '[:lower:]')" in
        y | yes) return 0 ;;
        *) return 1 ;;
    esac
}

open_url() {
    if [ "$(uname -s)" = Darwin ]; then
        open "$1" >/dev/null 2>&1 || true
    elif command -v xdg-open >/dev/null 2>&1; then
        xdg-open "$1" >/dev/null 2>&1 &
    fi
    printf '  If no browser opened, go to %s\n' "$1"
}

sh_quote() {
    printf "'%s'" "$(printf '%s' "$1" | sed "s/'/'\\\\''/g")"
}

fish_quote() {
    printf "'%s'" "$(printf '%s' "$1" | sed -e 's/\\/\\\\/g' -e "s/'/\\\\'/g")"
}

ask() {
    local name=$1 label=$2 purpose=$3 url=$4 steps=$5 check=$6
    local current="${!name-}" value result

    printf '\n%s%s\n' "$name" "$label"
    printf '  Used for: %s\n' "$purpose"

    if [ -n "$current" ] && ! confirm "Already set. Replace it? [y/N]" n; then
        return
    fi
    printf '  %s\n' "$steps"
    if [ -n "$url" ] && confirm "Open the page to create one in your browser? [Y/n]" y; then
        open_url "$url"
    fi

    while true; do
        if [ -n "$current" ]; then
            printf '  Paste it, or press Enter to keep the current one: '
        else
            printf '  Paste it, or press Enter to skip: '
        fi
        IFS= read -rs value || abort
        printf '\n'

        if [ -z "$value" ]; then
            break
        fi

        result=$("$check" "$value")
        case "$result" in
            200) current=$value; printf '  Accepted.\n'; break ;;
            000) current=$value; printf '  Could not reach the service to check it, keeping it anyway.\n'; break ;;
            *) printf '  Rejected (%s).\n' "$result" ;;
        esac
    done

    printf -v "$name" '%s' "$current"
}

cat <<EOF
Marlow's Crystal Optimizer publishing setup

This helps you create every secret that ./gradlew publishMods needs, checks each one against its service, and
stores them as environment variables for new terminals. Pasted values are hidden.
EOF

if [ -f "$env_file" ]; then
    # shellcheck source=/dev/null
    . "$env_file"
fi

ask GITHUB_TOKEN "" \
    "Creating the GitHub release and attaching every jar to it" \
    "$github_token_url" \
    "Everything on the page is filled in except Repository access: choose Only select repositories, pick MarlowsCrystalOptimizer, then Generate token." \
    check_github
ask MODRINTH_TOKEN "" \
    "Uploading one Modrinth version per supported range" \
    "https://modrinth.com/settings/pats" \
    "Create a PAT with the Create versions, Read versions and Write versions scopes." \
    check_modrinth
ask CURSEFORGE_TOKEN "" \
    "Uploading one CurseForge file per supported range" \
    "https://authors-old.curseforge.com/account/api-tokens" \
    "Generate a token with any name. CurseForge tokens have no permissions to choose." \
    check_curseforge
ask DISCORD_WEBHOOK "" \
    "Announcing the release in Discord" \
    "" \
    "In Discord: Edit Channel > Integrations > Webhooks > New Webhook > Copy Webhook URL." \
    check_discord
ask DISCORD_WEBHOOK_DRY_RUN " (optional)" \
    "Previewing the announcement from a dry run, in a test channel" \
    "" \
    "In Discord, for a test channel: Edit Channel > Integrations > Webhooks > New Webhook > Copy Webhook URL." \
    check_discord

mkdir -p "$config_dir"
umask 077
{
    printf '# Written by scripts/setup-publishing.sh in %s\n' "$repository"
    for name in $names; do
        if [ -n "${!name-}" ]; then
            printf 'export %s=%s\n' "$name" "$(sh_quote "${!name}")"
        fi
    done
} >"$env_file"
chmod 600 "$env_file"

hook="[ -f \"$env_file\" ] && . \"$env_file\""
case "$(basename "${SHELL:-sh}")" in
    zsh) rc="${ZDOTDIR:-$HOME}/.zshrc" ;;
    # macOS opens every terminal as a login shell, which reads .bash_profile and skips .bashrc.
    bash) if [ "$(uname -s)" = Darwin ]; then rc="$HOME/.bash_profile"; else rc="$HOME/.bashrc"; fi ;;
    fish) rc="" ;;
    *) rc="$HOME/.profile" ;;
esac

if [ -n "$rc" ]; then
    if ! grep -qF "$env_file" "$rc" 2>/dev/null; then
        printf "\n# Publishing secrets for Marlow's Crystal Optimizer\n%s\n" "$hook" >>"$rc"
    fi
    loaded_by=$rc
else
    # fish cannot read the export syntax of the shared file, so it gets its own copy.
    loaded_by="${XDG_CONFIG_HOME:-$HOME/.config}/fish/conf.d/marlowcrystal-publishing.fish"
    mkdir -p "$(dirname "$loaded_by")"
    {
        for name in $names; do
            if [ -n "${!name-}" ]; then
                printf 'set -gx %s %s\n' "$name" "$(fish_quote "${!name}")"
            fi
        done
    } >"$loaded_by"
    chmod 600 "$loaded_by"
fi

missing=""
for name in GITHUB_TOKEN MODRINTH_TOKEN CURSEFORGE_TOKEN DISCORD_WEBHOOK; do
    if [ -z "${!name-}" ]; then
        missing="$missing $name"
    fi
done

printf '\nSaved to %s, loaded by %s.\n' "$env_file" "$loaded_by"
printf 'Open a new terminal, and restart your IDE, before publishing.\n'
if [ -n "$missing" ]; then
    printf 'Still missing:%s. publishMods will not upload anything until they are set.\n' "$missing"
fi
