
#!/bin/bash

# WARNING: This script will DELETE ALL Elastic Beanstalk applications and environments
# Use with extreme caution!

set -e

echo "⚠️  WARNING: This script will DELETE ALL Elastic Beanstalk applications and environments!"
echo "This action cannot be undone."
echo ""
read -p "Are you sure you want to continue? (type 'DELETE' to confirm): " confirmation

if [ "$confirmation" != "DELETE" ]; then
    echo "Operation cancelled."
    exit 0
fi

echo ""
echo "🔍 Finding all Elastic Beanstalk environments..."

# Get all environments
ENVIRONMENTS=$(aws elasticbeanstalk describe-environments --query 'Environments[?Status!=`Terminated`].[EnvironmentName,Status,ApplicationName]' --output text)

if [ -z "$ENVIRONMENTS" ]; then
    echo "No active environments found."
else
    echo "Found environments:"
    echo "$ENVIRONMENTS"
    echo ""
    
    # Terminate all environments
    echo "🗑️  Terminating all environments..."
    while IFS=$'\t' read -r env_name status app_name; do
        if [ -n "$env_name" ] && [ "$status" != "Terminated" ]; then
            echo "Terminating environment: $env_name (from app: $app_name)"
            aws elasticbeanstalk terminate-environment --environment-name "$env_name" --terminate-resources
        fi
    done <<< "$ENVIRONMENTS"
    
    echo ""
    echo "⏳ Waiting for all environments to terminate..."
    
    # Wait for all environments to terminate
    while IFS=$'\t' read -r env_name status app_name; do
        if [ -n "$env_name" ] && [ "$status" != "Terminated" ]; then
            echo "Waiting for $env_name to terminate..."
            aws elasticbeanstalk wait environment-terminated --environment-names "$env_name"
            echo "✅ $env_name terminated"
        fi
    done <<< "$ENVIRONMENTS"
fi

echo ""
echo "🔍 Finding all Elastic Beanstalk applications..."

# Get all applications
APPLICATIONS=$(aws elasticbeanstalk describe-applications --query 'Applications[].ApplicationName' --output text)

if [ -z "$APPLICATIONS" ]; then
    echo "No applications found."
else
    echo "Found applications: $APPLICATIONS"
    echo ""
    
    # Delete all applications
    echo "🗑️  Deleting all applications..."
    for app_name in $APPLICATIONS; do
        echo "Deleting application: $app_name"
        aws elasticbeanstalk delete-application --application-name "$app_name" --terminate-env-by-force
        echo "✅ $app_name deleted"
    done
fi

echo ""
echo "🧹 Cleanup complete!"
echo "All Elastic Beanstalk applications and environments have been deleted."