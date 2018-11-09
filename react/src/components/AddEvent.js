import React from 'react';
import $ from 'jquery';
import { Form, Icon, Input, Button, message } from 'antd';
import { API_ROOT } from '../constants';
import PropTypes from 'prop-types';
import {Log} from './Log';
import {LogData} from './LogData'
import {ResponseLogData} from './ResponseLogData'
const FormItem = Form.Item;


class AddEventForm extends React.Component {
    state = {
        confirmDirty: false,
        autoCompleteResult: [],
    };
    handleSubmit = (e) => {
        e.preventDefault();
        this.props.form.validateFieldsAndScroll((err, values) => {
            if (!err) {
                console.log('Received values of form: ', values);

                var d = LogData.getData();
                var rld = ResponseLogData.getData();
                var temp = {number: d.length, content:'added event: '+ values.eventRank + ',' + values.vehicleID};
                d.push(temp);
                LogData.setData(d);
                var parent = this;
                this.props.transferMsg(d);
                $.ajax({
                    url: `${API_ROOT}/command`,
                    method: 'POST',
                    data: 'add_event,'+ values.eventRank + ',' + values.vehicleID,

                }).then((response) => {
                    message.success("stop extended to route");
                    var tmp = {number: rld.length, content:response};
                    rld.push(tmp);
                    //this.props.transferMsg(d, rld).bind(window);
                    parent.props.transferMsg(d, rld);
                }, (response) => {
                    message.error(response.responseText);
                }).catch((error) => {
                    message.error(error);
                });
            }
        });
    }

    checkNum = (rule, value, callback) => {
        const form = this.props.form;
        if (value && !(value < Number.MAX_SAFE_INTEGER && value >= 0 )) {
            callback('Invalid Number');
        } else {
            callback();
        }
    }
    checkConfirm = (rule, value, callback) => {
        const form = this.props.form;
        if (value && this.state.confirmDirty) {
            form.validateFields(['confirm'], { force: true });
        }
        callback();
    }
    render() {
        const { getFieldDecorator } = this.props.form;
        const formItemLayout = {
            labelCol: {
                xs: { span: 24 },
                sm: { span: 8 },
            },
            wrapperCol: {
                xs: { span: 24 },
                sm: { span: 16 },
            },
        };
        const tailFormItemLayout = {
            wrapperCol: {
                xs: {
                    span: 24,
                    offset: 0,
                },
                sm: {
                    span: 16,
                    offset: 8,
                },
            },
        };
        return (
            <Form onSubmit={this.handleSubmit} className="multisim-form">
                <FormItem {...tailFormItemLayout}>
                    <Button type="primary" htmlType="submit">Add Move_Vehicle Event</Button>
                </FormItem>

                <FormItem
                    {...formItemLayout}
                    label="Event Rank"
                >
                    {getFieldDecorator('eventRank', {
                        rules: [{ required: true, message: 'Please input Event Rank!', whitespace: true }, {
                            validator: this.checkNum,
                        }],
                    })(
                        <Input />
                    )}
                </FormItem>

                <FormItem
                    {...formItemLayout}
                    label="Vehicle ID"
                >
                    {getFieldDecorator('vehicleID', {
                        rules: [{ required: true, message: 'Please input Event ID!', whitespace: true }, {
                            validator: this.checkNum,
                        }],
                    })(
                        <Input />
                    )}
                </FormItem>

            </Form>
        );
    }
}


export const AddEvent = Form.create()(AddEventForm);