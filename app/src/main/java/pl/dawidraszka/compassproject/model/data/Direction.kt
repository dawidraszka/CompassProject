package pl.dawidraszka.compassproject.model.data

data class Direction(val angle: Int) {

    private val earthDirection: EarthDirection = when (angle) {
        in 23..67 -> EarthDirection.NORTHEAST
        in 68..112 -> EarthDirection.EAST
        in 113..157 -> EarthDirection.SOUTHEAST
        in 158..202 -> EarthDirection.SOUTH
        in 203..247 -> EarthDirection.SOUTHWEST
        in 248..292 -> EarthDirection.WEST
        in 293..337 -> EarthDirection.NORTHWEST
        else -> EarthDirection.NORTH
    }

    override fun toString(): String = "$angle° ${earthDirection.abbreviation}"
}

enum class EarthDirection(val abbreviation: String) {
    NORTH("N"),
    NORTHEAST("NE"),
    EAST("E"),
    SOUTHEAST("SE"),
    SOUTH("S"),
    SOUTHWEST("SW"),
    WEST("W"),
    NORTHWEST("NW")
}