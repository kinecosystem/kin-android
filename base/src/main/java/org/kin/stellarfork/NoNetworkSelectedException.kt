package org.kin.stellarfork

/**
 * Indicates that no network was selected.
 */
class NoNetworkSelectedException : RuntimeException("No network selected. Use `Network.use`, `Network.usePublicNetwork` or `Network.useTestNetwork` helper methods to select network.")
