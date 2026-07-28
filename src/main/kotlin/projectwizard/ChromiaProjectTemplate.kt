package net.postchain.rellide.jetbrains.projectwizard

/** Project templates offered by `chr create-rell-dapp --template=...`. */
enum class ChromiaProjectTemplate(
    val cliName: String,
    val displayName: String,
    val description: String,
) {
    MINIMAL(
        "minimal",
        "Sample dapp",
        "A small working dapp with sample queries, operations, and tests.",
    ),
    PLAIN(
        "plain",
        "Empty dapp",
        "An empty skeleton with blank main and test files.",
    ),
    PLAIN_MULTI(
        "plain-multi",
        "Empty dapp (multiple modules)",
        "An empty skeleton with blank main and test files split into multiple modules.",
    ),
    PLAIN_LIBRARY(
        "plain-library",
        "Library",
        "An empty skeleton structured for Rell library development.",
    ),
    ASSET_MANAGEMENT(
        "asset-management",
        "Asset management dapp",
        "A dapp focused on asset management on the Chromia blockchain, including " +
                "blockchain operations and a frontend for user interaction.",
    ),
}
