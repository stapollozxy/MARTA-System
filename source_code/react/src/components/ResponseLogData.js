export class ResponseLogData {
    static data = [] ;
    static getData() {
        return this.data;
    }
    static setData(newData) {
        this.data = newData;
    }
    static addData(newData) {
        this.data.push(newData);
    }
    static deleteData() {
        this.data = [];
    }
}