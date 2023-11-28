function rand {
    RANGE=${1:-100}
    echo $(( $RANDOM % $RANGE + 1 ))
}

function gen_leader {
    NAN_PROB=${1:-1000000}
    echo "sensor-id,humidity"
    for j in {0..100}
    do
        if [[ 1 == $(rand $NAN_PROB) ]]
        then
            echo "s$(rand 1000),NaN"
        else
            echo "s$(rand 1000),$(rand)"
        fi
    done
}

mkdir data
cd data

for i in {0..10000}
do
    echo $i
    gen_leader > "leader-$i.csv"
done
echo NaN
gen_leader 1 > "leader-101.csv"
