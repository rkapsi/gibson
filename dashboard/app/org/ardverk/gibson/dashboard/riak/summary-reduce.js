function(values) {
    var r = {};
    for (var value in values) {
        ejsLog('/tmp/map_reduce.log', JSON.stringify(values[value]));
        if (!values[value].event) {
            continue;
        }
        var key = values[value].event.throwable.className;
        if (!r[key]) {
            r[key] = 0;
        }
        r[key]++;
    }
    return [r];
}

