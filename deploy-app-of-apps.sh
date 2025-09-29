#!/bin/bash

set -e

echo "Deploying Encryptify App of Apps to ArgoCD..."

if ! command -v kubectl &> /dev/null; then
    echo "kubectl is not installed or not in PATH"
    exit 1
fi

if ! command -v argocd &> /dev/null; then
    echo "ArgoCD CLI not found. You can install it from: https://argo-cd.readthedocs.io/en/stable/cli_installation/"
    echo "   Or use kubectl to apply the configuration directly"
fi

echo "Creating main App of Apps application..."

cat <<EOF | kubectl apply -f -
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
  name: encryptify-app-of-apps
  namespace: argocd
  finalizers:
    - resources-finalizer.argocd.argoproj.io
spec:
  project: default
  source:
    repoURL: https://github.com/Day-fit/Encryptify-CD.git
    targetRevision: HEAD
    path: helm/ArgoCD-Configuration
    helm:
      releaseName: encryptify-app-of-apps
  destination:
    server: https://kubernetes.default.svc
    namespace: argocd
  syncPolicy:
    automated:
      prune: true
      selfHeal: true
    syncOptions:
      - CreateNamespace=true
      - PrunePropagationPolicy=foreground
      - PruneLast=true
EOF

echo "App of Apps application created successfully!"

# Wait for the application to be synced
echo "Waiting for the application to sync..."
kubectl wait --for=condition=Synced application/encryptify-app-of-apps -n argocd --timeout=300s

# Check the status
echo "Application status:"
kubectl get application encryptify-app-of-apps -n argocd

echo ""
echo "Deployment complete!"
